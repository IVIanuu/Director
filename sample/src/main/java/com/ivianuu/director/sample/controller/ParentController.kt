package com.ivianuu.director.sample.controller

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.*
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.sample.R
import com.ivianuu.director.sample.util.ColorUtil

class ParentController : BaseController() {

    private var finishing = false
    private var hasShownAll = false

    override val layoutRes get() = R.layout.controller_parent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = "Parent/Child Demo"
        error("broken")
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        //   addChild(0)
    }

    private fun addChild(index: Int) {
        val frameId = resources.getIdentifier(
            "child_content_" + (index + 1),
            "id",
            context.packageName
        )

        val container = view!!.findViewById<ViewGroup>(frameId)

        getChildRouter(container, factory = ::StackRouter).let { childRouter ->
            childRouter as StackRouter // todo
            childRouter.popsLastView = true

            if (!childRouter.hasRoot) {
                val childController = ChildController.newInstance(
                    "Child Controller #$index",
                    ColorUtil.getMaterialColor(resources, index),
                    false
                )

                /* childController.doOnChangeEnd { _, _, _, changeType ->
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
                                 router.pop(this@ParentController)
                             }
                         }
                     }
                 }*/

                childRouter.setRoot(
                    childController.toTransaction()
                        .changeHandler(FadeChangeHandler())
                )
            }
        }
    }

    private fun removeChild(index: Int) {
        val childRouters = childRouterManager.routers.toList()
        if (index < childRouters.size) {
            removeChildRouter(childRouters[index])
        }
    }

    override fun handleBack(): Boolean {
        // todo
        val childControllers = childRouterManager.routers
            .filterIsInstance<StackRouter>().count(StackRouter::hasRoot)

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
