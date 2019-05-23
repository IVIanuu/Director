package com.ivianuu.director

import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.ivianuu.director.ControllerState.ATTACHED
import com.ivianuu.director.ControllerState.CREATED
import com.ivianuu.director.ControllerState.DESTROYED
import com.ivianuu.director.ControllerState.INITIALIZED
import com.ivianuu.director.ControllerState.VIEW_CREATED
import com.ivianuu.director.internal.ControllerViewModelStores
import com.ivianuu.director.internal.classForNameOrThrow
import java.util.*

/**
 * Lightweight view controller with a lifecycle
 */
abstract class Controller : LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    /**
     * The arguments of this controller
     */
    var args = Bundle(javaClass.classLoader)

    /**
     * The router of this controller
     */
    val router: Router
        get() {
            check(this::_router.isInitialized) {
                "router is only available after onCreate"
            }

            return _router
        }

    private lateinit var _router: Router

    /**
     * The router manager of the router this controller is attached to
     */
    val routerManager: RouterManager get() = router.routerManager

    /**
     * The hosting activity
     */
    val activity: FragmentActivity get() = routerManager.activity

    /**
     * The parent controller if any
     */
    val parentController: Controller? get() = routerManager.parent

    /**
     * The view of this controller or null
     */
    var view: View? = null
        private set

    /**
     * The instance id of this controller
     */
    var instanceId = UUID.randomUUID().toString()
        private set

    private val _viewModelStore by lazy(LazyThreadSafetyMode.NONE) {
        ControllerViewModelStores.get(this).getViewModelStore(instanceId)
    }

    private val lifecycleRegistry = LifecycleRegistry(this)

    /**
     * The [LifecycleOwner] of the view
     */
    val viewLifecycleOwner: LifecycleOwner
        get() =
            _viewLifecycleOwner
                ?: error("can only access the viewLifecycleOwner while the view is created")
    private var _viewLifecycleOwner: ControllerViewLifecycleOwner? = null

    private val savedStateRegistryController =
        SavedStateRegistryController.create(this)

    /**
     * The transaction index of this controller
     */
    var transactionIndex: Int = -1
        internal set

    /**
     * The tag of this controller
     */
    var tag: String? = null
        set(value) {
            check(isRestoring || !this::_router.isInitialized) {
                "Cannot be changed after being added to a router"
            }
            field = value
        }

    /**
     * The current state of this controller
     */
    var state: ControllerState = INITIALIZED
        private set

    private var allState: Bundle? = null
    private var viewState: Bundle? = null
    private var isRestoring = false

    /**
     * The change handler being used when this controller enters the screen
     */
    var pushChangeHandler: ControllerChangeHandler? = DirectorPlugins.defaultPushHandler
        set(value) {
            check(isRestoring || !this::_router.isInitialized) {
                "Cannot be changed after being added to a router"
            }
            field = value
        }

    /**
     * The change handler being used when this controller exits the screen
     */
    var popChangeHandler: ControllerChangeHandler? = DirectorPlugins.defaultPopHandler
        set(value) {
            check(isRestoring || !this::_router.isInitialized) {
                "Cannot be changed after being added to a router"
            }
            field = value
        }

    /**
     * The child router manager of this controller
     */
    val childRouterManager by lazy(LazyThreadSafetyMode.NONE) {
        RouterManager(activity, this, true)
    }

    private val listeners = mutableListOf<ControllerLifecycleListener>()

    private var superCalled = false

    /**
     * Will be called once when this controller gets attached to its router
     */
    protected open fun onCreate(savedInstanceState: Bundle?) {
        superCalled = true
        // restore the full instance state of child routers
        childRouterManager.startPostponedFullRestore()
    }

    /**
     * Called when this Controller has been destroyed.
     */
    protected open fun onDestroy() {
        superCalled = true
    }

    /**
     * Creates the view for this controller
     */
    protected abstract fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View

    /**
     * Called when the view of this controller gets destroyed
     */
    protected open fun onDestroyView(view: View) {
        superCalled = true
    }

    /**
     * Called when this Controller is attached to its container
     */
    protected open fun onAttach(view: View) {
        superCalled = true
    }

    /**
     * Called when this Controller is detached from its container
     */
    protected open fun onDetach(view: View) {
        superCalled = true
    }

    /**
     * Will be called when the instance state gets restores
     */
    protected open fun onRestoreInstanceState(savedInstanceState: Bundle) {
        superCalled = true
    }

    /**
     * Called to save the instance state of this controller
     */
    protected open fun onSaveInstanceState(outState: Bundle) {
        superCalled = true
    }

    /**
     * Will be called when the view state gets restored
     */
    protected open fun onRestoreViewState(view: View, savedViewState: Bundle) {
        superCalled = true
    }

    /**
     * Called to save the view state of this controller
     */
    protected open fun onSaveViewState(view: View, outState: Bundle) {
        superCalled = true
    }

    /**
     * Called when this Controller begins the process of being swapped in or out of the host view.
     */
    protected open fun onChangeStarted(
        other: Controller?,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        superCalled = true
    }

    /**
     * Called when this Controller completes the process of being swapped in or out of the host view.
     */
    protected open fun onChangeEnded(
        other: Controller?,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        superCalled = true
    }

    /**
     * Should be overridden if this Controller needs to handle the back button being pressed.
     */
    open fun handleBack(): Boolean = childRouterManager.handleBack()

    /**
     * Adds a listener for all of this Controller's lifecycle events
     */
    fun addLifecycleListener(listener: ControllerLifecycleListener) {
        listeners.add(listener)
    }

    /**
     * Removes the previously added [listener]
     */
    fun removeLifecycleListener(listener: ControllerLifecycleListener) {
        listeners.remove(listener)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun getSavedStateRegistry(): SavedStateRegistry =
        savedStateRegistryController.savedStateRegistry

    override fun getViewModelStore(): ViewModelStore = _viewModelStore

    internal fun create(router: Router) {
        _router = router

        allState?.let { restoreInternalState(it) }

        val instanceState = allState?.getBundle(KEY_SAVED_STATE)
            ?.also { it.classLoader = this::class.java.classLoader }

        // create
        notifyListeners { it.preCreate(this, instanceState) }

        state = CREATED
        savedStateRegistryController.performRestore(instanceState)

        requireSuperCalled { onCreate(instanceState) }

        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        notifyListeners { it.postCreate(this, instanceState) }

        instanceState?.let { restoreUserInstanceState(it) }
        allState = null
    }

    internal fun destroy() {
        childRouterManager.onDestroy()

        notifyListeners { it.preDestroy(this) }

        state = DESTROYED
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED

        requireSuperCalled { onDestroy() }

        notifyListeners { it.postDestroy(this) }

        if (!activity.isChangingConfigurations) {
            ControllerViewModelStores.get(this)
                .removeViewModelStore(instanceId)
        }
    }

    internal fun createView(container: ViewGroup): View {
        _viewLifecycleOwner = ControllerViewLifecycleOwner()

        val savedViewState = viewState?.getBundle(KEY_VIEW_STATE_BUNDLE)
            ?.also { it.classLoader = this::class.java.classLoader }

        notifyListeners { it.preCreateView(this, savedViewState) }

        val view = onCreateView(
            LayoutInflater.from(container.context),
            container,
            savedViewState
        ).also { this.view = it }

        state = VIEW_CREATED
        _viewLifecycleOwner!!.currentState = Lifecycle.State.CREATED

        notifyListeners { it.postCreateView(this, view, savedViewState) }

        // restore hierarchy
        viewState
            ?.getSparseParcelableArray<Parcelable>(KEY_VIEW_STATE_HIERARCHY)
            ?.let { view.restoreHierarchyState(it) }

        if (savedViewState != null) {
            requireSuperCalled { onRestoreViewState(view, savedViewState) }
            notifyListeners { it.onRestoreViewState(this, view, savedViewState) }
        }

        viewState = null

        (view as? ViewGroup)?.let { childRouterManager.setRootView(it) }

        return view
    }

    internal fun destroyView(saveViewState: Boolean) {
        val view = requireView()
        if (saveViewState && viewState == null) {
            saveViewState()
        }

        childRouterManager.removeRootView()

        notifyListeners { it.preDestroyView(this, view) }

        _viewLifecycleOwner!!.currentState = Lifecycle.State.DESTROYED
        requireSuperCalled { onDestroyView(view) }
        state = CREATED
        _viewLifecycleOwner = null
        this.view = null

        notifyListeners { it.postDestroyView(this) }
    }

    internal fun attach() {
        val view = requireView()

        notifyListeners { it.preAttach(this, view) }

        state = ATTACHED

        requireSuperCalled { onAttach(view) }

        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        _viewLifecycleOwner!!.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        _viewLifecycleOwner!!.currentState = Lifecycle.State.RESUMED

        notifyListeners { it.postAttach(this, view) }

        viewState = null

        childRouterManager.onStart()
    }

    internal fun detach() {
        val view = requireView()

        childRouterManager.onStop()

        notifyListeners { it.preDetach(this, view) }

        state = VIEW_CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        _viewLifecycleOwner!!.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        _viewLifecycleOwner!!.currentState = Lifecycle.State.CREATED

        requireSuperCalled { onDetach(view) }

        notifyListeners { it.postDetach(this, view) }
    }

    internal fun saveInstanceState(): Bundle {
        if (view != null && viewState == null) {
            saveViewState()
        }

        val outState = Bundle(this::class.java.classLoader)
        outState.putString(KEY_CLASS_NAME, javaClass.name)
        outState.putBundle(KEY_VIEW_STATE, viewState)
        outState.putBundle(KEY_ARGS, args)
        outState.putString(KEY_INSTANCE_ID, instanceId)
        outState.putString(KEY_TAG, tag)
        outState.putInt(KEY_TRANSACTION_INDEX, transactionIndex)

        pushChangeHandler?.let { outState.putBundle(KEY_PUSH_CHANGE_HANDLER, it.toBundle()) }
        popChangeHandler?.let { outState.putBundle(KEY_POP_CHANGE_HANDLER, it.toBundle()) }

        val savedState = Bundle(this::class.java.classLoader)
        requireSuperCalled { onSaveInstanceState(savedState) }
        savedStateRegistryController.performSave(savedState)
        notifyListeners { it.onSaveInstanceState(this, savedState) }
        outState.putBundle(KEY_SAVED_STATE, savedState)

        outState.putBundle(KEY_CHILD_ROUTER_STATES, childRouterManager.saveInstanceState())

        return outState
    }

    private fun restoreInternalState(savedInstanceState: Bundle) {
        isRestoring = true
        args = savedInstanceState.getBundle(KEY_ARGS)!!
            .also { it.classLoader = this::class.java.classLoader }

        viewState = savedInstanceState.getBundle(KEY_VIEW_STATE)
            ?.also { it.classLoader = this::class.java.classLoader }

        instanceId = savedInstanceState.getString(KEY_INSTANCE_ID)!!
        tag = savedInstanceState.getString(KEY_TAG)
        transactionIndex = savedInstanceState.getInt(KEY_TRANSACTION_INDEX)

        pushChangeHandler = savedInstanceState.getBundle(KEY_PUSH_CHANGE_HANDLER)
            ?.let { ControllerChangeHandler.fromBundle(it) }
        popChangeHandler = savedInstanceState.getBundle(KEY_POP_CHANGE_HANDLER)
            ?.let { ControllerChangeHandler.fromBundle(it) }

        childRouterManager.restoreInstanceState(
            savedInstanceState.getBundle(KEY_CHILD_ROUTER_STATES)!!
        )

        isRestoring = false
    }

    private fun restoreUserInstanceState(savedInstanceState: Bundle) {
        requireSuperCalled { onRestoreInstanceState(savedInstanceState) }
        notifyListeners { it.onRestoreInstanceState(this, savedInstanceState) }
    }

    private fun saveViewState() {
        val view = requireView()

        val viewState = Bundle()
            .also { it.classLoader = this::class.java.classLoader }
            .also { this.viewState = it }

        val hierarchyState = SparseArray<Parcelable>()
        view.saveHierarchyState(hierarchyState)
        viewState.putSparseParcelableArray(KEY_VIEW_STATE_HIERARCHY, hierarchyState)

        val stateBundle = Bundle()
            .also { it.classLoader = this::class.java.classLoader }
        requireSuperCalled { onSaveViewState(view, stateBundle) }
        viewState.putBundle(KEY_VIEW_STATE_BUNDLE, stateBundle)

        notifyListeners { it.onSaveViewState(this, view, viewState) }
    }

    internal fun changeStarted(
        other: Controller?,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        requireSuperCalled { onChangeStarted(other, changeHandler, changeType) }
        notifyListeners { it.onChangeStarted(this, other, changeHandler, changeType) }
    }

    internal fun changeEnded(
        other: Controller?,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        requireSuperCalled { onChangeEnded(other, changeHandler, changeType) }
        notifyListeners { it.onChangeEnded(this, other, changeHandler, changeType) }
    }

    private inline fun notifyListeners(block: (ControllerLifecycleListener) -> Unit) {
        (listeners + router.getControllerLifecycleListeners()).forEach(block)
    }

    private inline fun requireSuperCalled(block: () -> Unit) {
        superCalled = false
        block()
        check(superCalled) { "super not called ${javaClass.name}" }
    }

    private class ControllerViewLifecycleOwner : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        var currentState: Lifecycle.State
            get() = lifecycleRegistry.currentState
            set(value) {
                lifecycleRegistry.currentState = value
            }

        override fun getLifecycle(): Lifecycle = lifecycleRegistry
    }

    companion object {
        private const val KEY_CLASS_NAME = "Controller.className"
        private const val KEY_VIEW_STATE = "Controller.viewState"
        private const val KEY_CHILD_ROUTER_STATES = "Controller.childRouterStates"
        private const val KEY_SAVED_STATE = "Controller.savedState"
        private const val KEY_INSTANCE_ID = "Controller.instanceId"
        private const val KEY_TAG = "Controller.tag"
        private const val KEY_TRANSACTION_INDEX = "Controller.transactionIndex"
        private const val KEY_PUSH_CHANGE_HANDLER = "Controller.pushChangeHandler"
        private const val KEY_POP_CHANGE_HANDLER = "Controller.popChangeHandler"
        private const val KEY_ARGS = "Controller.args"
        private const val KEY_VIEW_STATE_HIERARCHY = "Controller.viewState.hierarchy"
        private const val KEY_VIEW_STATE_BUNDLE = "Controller.viewState.bundle"

        internal fun fromBundle(bundle: Bundle, factory: ControllerFactory): Controller {
            val className = bundle.getString(KEY_CLASS_NAME)!!
            val cls = classForNameOrThrow(className)

            return factory.createController(cls.classLoader!!, className).apply {
                this.allState = bundle.also {
                    it.classLoader = this::class.java.classLoader
                }
            }
        }
    }
}

val Controller.isCreated: Boolean get() = state.isAtLeast(CREATED)

val Controller.isViewCreated: Boolean get() = state.isAtLeast(VIEW_CREATED)

val Controller.isAttached: Boolean get() = state.isAtLeast(ATTACHED)

val Controller.isDestroyed: Boolean get() = state == DESTROYED

val Controller.application: Application
    get() = activity.application

val Controller.resources: Resources get() = activity.resources

fun Controller.requireView(): View =
    view ?: error("view is only accessible between onCreateView and onDestroyView")

val Controller.childRouters: List<Router> get() = childRouterManager.routers

fun Controller.getChildRouterOrNull(
    containerId: Int,
    tag: String?
): Router? = childRouterManager.getRouterOrNull(containerId, tag)

fun Controller.getChildRouter(
    containerId: Int,
    tag: String? = null
): Router = childRouterManager.getRouter(containerId, tag)

fun Controller.removeChildRouter(childRouter: Router) {
    childRouterManager.removeRouter(childRouter)
}

fun Controller.getChildRouterOrNull(
    container: ViewGroup,
    tag: String? = null
): Router? = getChildRouterOrNull(container.id, tag)

fun Controller.getChildRouter(
    container: ViewGroup,
    tag: String? = null
): Router = getChildRouter(container.id, tag)

fun Controller.childRouter(
    containerId: Int,
    tag: String? = null
): Lazy<Router> = lazy(LazyThreadSafetyMode.NONE) { getChildRouter(containerId, tag) }

fun Controller.tag(tag: String?): Controller = apply { this.tag = tag }

fun Controller.pushChangeHandler(changeHandler: ControllerChangeHandler?): Controller =
    apply {
        pushChangeHandler = changeHandler
    }

fun Controller.popChangeHandler(changeHandler: ControllerChangeHandler?): Controller =
    apply {
        popChangeHandler = changeHandler
    }

fun Controller.changeHandler(changeHandler: ControllerChangeHandler?): Controller =
    pushChangeHandler(changeHandler).popChangeHandler(changeHandler)