package com.ivianuu.director.internal

import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.*

internal object ControllerChangeManager {

    private val handlers = mutableMapOf<String, ChangeHandler>()

    fun executeChange(
        router: Router,
        from: Controller?,
        to: Controller?,
        isPush: Boolean,
        container: ViewGroup,
        handler: ChangeHandler?,
        forceRemoveFromViewOnPush: Boolean,
        listeners: List<RouterListener>
    ) {
        val handlerToUse = when {
            handler == null -> DefaultChangeHandler()
            handler.hasBeenUsed -> handler.copy()
            else -> handler
        }
        handlerToUse.hasBeenUsed = true

        if (from != null) {
            cancelChange(from.instanceId)
        }

        if (to != null) {
            handlers[to.instanceId] = handlerToUse
        }

        listeners.forEach { it.onChangeStarted(router, to, from, isPush, container, handlerToUse) }

        val toView = to?.createView(container)
        val fromView = from?.view

        val toIndex = getToIndex(router, container, toView, fromView, isPush)

        val callback = object : ChangeHandler.Callback {
            override fun addToView() {
                val addingToView = toView != null && toView.parent == null
                val movingToView = toView != null && container.indexOfChild(toView) != toIndex
                if (addingToView) {
                    container.addView(toView, toIndex)
                } else if (movingToView) {
                    container.moveView(toView!!, toIndex)
                }
            }

            override fun removeFromView() {
                if (fromView != null && (!isPush || handlerToUse.removesFromViewOnPush
                            || forceRemoveFromViewOnPush)
                ) {
                    container.removeView(fromView)
                }
            }

            override fun onChangeCompleted() {
                if (to != null) {
                    handlers.remove(to.instanceId)
                }

                listeners.forEach {
                    it.onChangeCompleted(
                        router,
                        to,
                        from,
                        isPush,
                        container,
                        handlerToUse
                    )
                }
            }
        }

        val changeData = ChangeData(
            container,
            fromView,
            toView,
            isPush,
            callback,
            toIndex,
            forceRemoveFromViewOnPush
        )

        handlerToUse.performChange(changeData)
    }

    fun cancelChange(instanceId: String) {
        handlers.remove(instanceId)?.cancel()
    }

    private fun getToIndex(
        router: Router,
        container: ViewGroup,
        to: View?,
        from: View?,
        isPush: Boolean
    ): Int {
        if (to == null) return -1
        return if (isPush || from == null) {
            if (container.childCount == 0) return -1
            val backstackIndex = router.backstack.indexOfFirst { it.controller.view == to }
            (0 until container.childCount)
                .map(container::getChildAt)
                .indexOfFirst { v ->
                    router.backstack.indexOfFirst {
                        it.controller.view == v
                    } > backstackIndex
                }
        } else {
            val currentToIndex = container.indexOfChild(to)
            val currentFromIndex = container.indexOfChild(from)

            if (currentToIndex == -1 || currentToIndex > currentFromIndex) {
                container.indexOfChild(from)
            } else {
                currentToIndex
            }
        }
    }

}