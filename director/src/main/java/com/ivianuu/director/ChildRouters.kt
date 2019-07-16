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

package com.ivianuu.director

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

fun Controller.childRouter(container: ViewGroup): Router =
    childRouter { container }

fun Controller.childRouter(containerId: Int): Router =
    childRouter { view!!.findViewById(containerId) }

fun Controller.childRouter(containerProvider: (() -> ViewGroup)? = null): Router {
    val router = Router(this)

    if (lifecycle.currentState == DESTROYED) {
        router.destroy()
        return router
    }

    if (view != null) {
        containerProvider?.invoke()?.let { router.setContainer(it) }
    }

    if (lifecycle.currentState.isAtLeast(STARTED)) {
        router.start()
    }

    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    containerProvider?.invoke()?.let { router.setContainer(it) }
                }
                Lifecycle.Event.ON_RESUME -> {
                    router.start()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    router.stop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    if (containerProvider != null) {
                        router.removeContainer()
                    }

                    router.destroy()
                }
            }
        }
    })

    return router
}