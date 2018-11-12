/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.director.common.changehandler

import android.os.Build
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerChangeHandler

/**
 * A base [ControllerChangeHandler] that facilitates using [android.transition.Transition]s to replace Controller Views.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
abstract class TransitionChangeHandler : ControllerChangeHandler() {

    private var canceled = false
    private var needsImmediateCompletion = false

    override val removesFromViewOnPush get() = true

    override fun performChange(
        container: ViewGroup,
        from: View?,
        to: View?,
        isPush: Boolean,
        onChangeComplete: () -> Unit
    ) {
        if (canceled) {
            onChangeComplete()
            return
        }

        if (needsImmediateCompletion) {
            executePropertyChanges(container, from, to, null, isPush)
            onChangeComplete()
            return
        }

        val transition = getTransition(container, from, to, isPush)
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionCancel(transition: Transition) {
                onChangeComplete()
            }

            override fun onTransitionEnd(transition: Transition) {
                onChangeComplete()
            }
        })

        prepareForTransition(
            container,
            from,
            to,
            transition,
            isPush
        ) {
            if (!canceled) {
                TransitionManager.beginDelayedTransition(container, transition)
                executePropertyChanges(container, from, to, transition, isPush)
            }
        }
    }

    override fun onAbortPush(newHandler: ControllerChangeHandler, newTop: Controller?) {
        super.onAbortPush(newHandler, newTop)
        canceled = true
    }

    override fun completeImmediately() {
        super.completeImmediately()
        needsImmediateCompletion = true
    }

    /**
     * Should be overridden to return the Transition to use while replacing Views.
     */
    protected abstract fun getTransition(
        container: ViewGroup,
        from: View?,
        to: View?,
        isPush: Boolean
    ): Transition

    /**
     * Called before a transition occurs. This can be used to reorder views, set their transition names, etc. The transition will begin
     * when `onTransitionPreparedListener` is called.
     */
    protected open fun prepareForTransition(
        container: ViewGroup,
        from: View?,
        to: View?,
        transition: Transition,
        isPush: Boolean,
        onTransitionPrepared: () -> Unit
    ) {
        onTransitionPrepared()
    }

    /**
     * This should set all view properties needed for the transition to work properly. By default it removes the "from" view
     * and adds the "to" view.
     */
    protected open fun executePropertyChanges(
        container: ViewGroup,
        from: View?,
        to: View?,
        transition: Transition?,
        isPush: Boolean
    ) {
        if (from != null && (removesFromViewOnPush || !isPush) && from.parent == container) {
            container.removeView(from)
        }
        if (to != null && to.parent == null) {
            container.addView(to)
        }
    }
}