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

package com.ivianuu.director.sample.util

import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerChangeHandler
import com.ivianuu.director.Router
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.hasRootController

class SingleContainer(val router: Router) {

    val isEmpty get() = router.hasRootController

    val currentTransaction: RouterTransaction? get() = router.backstack.lastOrNull()
    val detachedTransactions: List<RouterTransaction>? get() = router.backstack.dropLast(1)

    fun set(transaction: RouterTransaction) {
        if (transaction == currentTransaction) return

        transaction.pushChangeHandler.check()
        transaction.popChangeHandler.check()

        val newBackstack = router.backstack.toMutableList()
        val index = newBackstack.indexOfFirst { it == transaction }

        if (index != -1) newBackstack.removeAt(index)
        newBackstack.add(transaction)

        router.setBackstack(newBackstack, isPush = true)
    }

    fun remove(controller: Controller) {
        val newBackstack = router.backstack.toMutableList()
        newBackstack.removeAll { it.controller == controller }
        router.setBackstack(newBackstack)
    }

    private fun ControllerChangeHandler?.check() {
        if (this != null) check(removesFromViewOnPush) {
            "Must remove from view while using single container"
        }
    }
}

inline fun SingleContainer.setByTag(tag: String, create: () -> RouterTransaction) {
    set(router.backstack.firstOrNull { it.tag == tag } ?: create())
}

fun SingleContainer.removeByTag(tag: String) {
    router.backstack.firstOrNull { it.tag == tag }?.let { remove(it.controller) }
}