package com.ivianuu.director

import android.os.Bundle

import com.ivianuu.director.internal.TransactionIndexer

/**
 * Metadata used for adding [Controller]s to a [Router].
 */
class RouterTransaction {

    /**
     * The controller of this transaction
     */
    val controller: Controller

    /**
     * The tag of this transaction
     */
    var tag: String? = null
        set(value) {
            checkModify()
            field = value
        }

    /**
     * The push change handler of this transaction
     */
    var pushChangeHandler: ControllerChangeHandler? = null
        get() = controller.overriddenPushHandler ?: field
        set(value) {
            checkModify()
            field = value
        }

    /**
     * The pop change handler of this transaction
     */
    var popChangeHandler: ControllerChangeHandler? = null
        get() = controller.overriddenPopHandler ?: field
        set(value) {
            checkModify()
            field = value
        }

    internal var transactionIndex = INVALID_INDEX
    internal var attachedToRouter = false

    internal constructor(controller: Controller, unit: Unit /*dummy*/) {
        this.controller = controller
    }

    private constructor(
        controller: Controller,
        pushChangeHandler: ControllerChangeHandler?,
        popChangeHandler: ControllerChangeHandler?,
        tag: String?,
        transactionIndex: Int,
        attachedToRouter: Boolean
    ) {
        this.controller = controller
        this.pushChangeHandler = pushChangeHandler
        this.popChangeHandler = popChangeHandler
        this.tag = tag
        this.transactionIndex = transactionIndex
        this.attachedToRouter = attachedToRouter
    }

    internal fun ensureValidIndex(indexer: TransactionIndexer) {
        if (transactionIndex == INVALID_INDEX) {
            transactionIndex = indexer.nextIndex()
        }
    }

    /**
     * Used to serialize this transaction into a Bundle
     */
    internal fun saveInstanceState() = Bundle().apply {
        putBundle(KEY_CONTROLLER_BUNDLE, controller.saveInstanceState())
        pushChangeHandler?.let { putBundle(KEY_PUSH_CHANGE_HANDLER, it.toBundle()) }
        popChangeHandler?.let { putBundle(KEY_POP_CHANGE_HANDLER, it.toBundle()) }
        putString(KEY_TAG, tag)
        putInt(KEY_INDEX, transactionIndex)
        putBoolean(KEY_ATTACHED_TO_ROUTER, attachedToRouter)
    }

    private fun checkModify() {
        if (attachedToRouter) {
            throw IllegalStateException("transactions cannot be modified after being added to a Router.")
        }
    }

    companion object {
        private const val KEY_CONTROLLER_BUNDLE = "RouterTransaction.controller.bundle"
        private const val KEY_PUSH_CHANGE_HANDLER = "RouterTransaction.pushChangeHandler"
        private const val KEY_POP_CHANGE_HANDLER = "RouterTransaction.popChangeHandler"
        private const val KEY_TAG = "RouterTransaction.tag"
        private const val KEY_INDEX = "RouterTransaction.transactionIndex"
        private const val KEY_ATTACHED_TO_ROUTER = "RouterTransaction.attachedToRouter"

        private const val INVALID_INDEX = -1

        internal fun fromBundle(bundle: Bundle, controllerFactory: ControllerFactory) =
            RouterTransaction(
                Controller.fromBundle(bundle.getBundle(KEY_CONTROLLER_BUNDLE)!!, controllerFactory),
            bundle.getBundle(KEY_PUSH_CHANGE_HANDLER)?.let { ControllerChangeHandler.fromBundle(it) },
            bundle.getBundle(KEY_POP_CHANGE_HANDLER)?.let { ControllerChangeHandler.fromBundle(it) },
            bundle.getString(KEY_TAG),
            bundle.getInt(KEY_INDEX),
            bundle.getBoolean(KEY_ATTACHED_TO_ROUTER)
        )
    }
}

/**
 * Returns a new [RouterTransaction] with the [controller]
 */
fun RouterTransaction(controller: Controller) = RouterTransaction(controller, Unit)

/**
 * Fluent version of push change handler
 */
fun RouterTransaction.pushChangeHandler(changeHandler: ControllerChangeHandler?) = apply {
    pushChangeHandler = changeHandler
}

/**
 * Fluent version of pop change handler
 */
fun RouterTransaction.popChangeHandler(changeHandler: ControllerChangeHandler?) = apply {
    popChangeHandler = changeHandler
}

/**
 * Fluent version of tag
 */
fun RouterTransaction.tag(tag: String?) = apply {
    this.tag = tag
}