package com.ivianuu.director.sample.controller

import android.view.ViewGroup
import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerChangeHandler
import com.ivianuu.director.ControllerChangeType
import com.ivianuu.director.ControllerLifecycleListener
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler
import com.ivianuu.director.requireActivity
import com.ivianuu.director.requireResources
import com.ivianuu.director.requireView
import com.ivianuu.director.sample.R
import com.ivianuu.director.sample.util.ColorUtil
import com.ivianuu.director.toTransaction

class ParentController : BaseController() {

    private var finishing = false
    private var hasShownAll = false

    override var title: String?
        get() = "Parent/Child Demo"
        set(value) {
            super.title = value
        }

    override val layoutRes = R.layout.controller_parent

    override fun onChangeEnded(
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        super.onChangeEnded(changeHandler, changeType)

        if (changeType == ControllerChangeType.PUSH_ENTER) {
            addChild(0)
        }
    }

    private fun addChild(index: Int) {
        val frameId = requireResources().getIdentifier(
            "child_content_" + (index + 1),
            "id",
            requireActivity().packageName
        )

        val container = requireView().findViewById<ViewGroup>(frameId)

        getChildRouter(container).let { childRouter ->
            childRouter.popsLastView = true

            if (!childRouter.hasRootController) {
                val childController = ChildController.newInstance(
                    "Child Controller #$index",
                    ColorUtil.getMaterialColor(requireResources(), index),
                    false
                )

                childController.addLifecycleListener(object : ControllerLifecycleListener {

                    override fun onChangeEnd(
                        controller: Controller,
                        changeHandler: ControllerChangeHandler,
                        changeType: ControllerChangeType
                    ) {
                        if (!isBeingDestroyed) {
                            if (changeType == ControllerChangeType.PUSH_ENTER && !hasShownAll) {
                                if (index < NUMBER_OF_CHILDREN - 1) {
                                    addChild(index + 1)
                                } else {
                                    hasShownAll = true
                                }
                            } else if (changeType == ControllerChangeType.POP_EXIT) {
                                if (index > 0) {
                                    removeChild(index - 1)
                                } else {
                                    router.popController(this@ParentController)
                                }
                            }
                        }
                    }
                })

                childRouter.setRoot(
                    childController.toTransaction()
                        .pushChangeHandler(FadeChangeHandler())
                        .popChangeHandler(FadeChangeHandler())
                )
            }
        }
    }

    private fun removeChild(index: Int) {
        val childRouters = childRouters
        if (index < childRouters.size) {
            removeChildRouter(childRouters[index])
        }
    }

    override fun handleBack(): Boolean {
        val childControllers = childRouters.count { it.hasRootController }

        return if (childControllers != NUMBER_OF_CHILDREN || finishing) {
            true
        } else {
            finishing = true
            super.handleBack()
        }
    }

    companion object {
        private const val NUMBER_OF_CHILDREN = 5
    }
}