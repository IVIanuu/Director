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

import com.ivianuu.director.ChangeHandler
import com.ivianuu.director.Controller
import com.ivianuu.director.Router
import com.ivianuu.director.Transaction
import com.ivianuu.director.hasRoot

class SingleContainer(val router: Router) {

    val isEmpty get() = router.hasRoot

    val currentTransaction: Transaction? get() = router.backstack.lastOrNull()
    val detachedTransactions: List<Transaction>? get() = router.backstack.dropLast(1)

    fun set(transaction: Transaction) {
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
        router.setBackstack(newBackstack, false)
    }

    private fun ChangeHandler?.check() {
        check(this?.removesFromViewOnPush ?: true) {
            "Must remove from view while using single container"
        }
    }
}

inline fun Router.moveToTop(tag: String, create: () -> Transaction) {
    val backstack = backstack.toMutableList()
    var transaction = backstack.firstOrNull { it.tag == tag }
    if (transaction != null) {
        backstack.remove(transaction)
    } else {
        transaction = create()
    }

    backstack.add(transaction)
    setBackstack(backstack, true)
}

inline fun SingleContainer.setIfEmpty(create: () -> Transaction) {
    if (isEmpty) set(create())
}

inline fun SingleContainer.setByTag(tag: String, create: () -> Transaction) {
    set(router.backstack.firstOrNull { it.tag == tag } ?: create())
}

fun SingleContainer.removeByTag(tag: String) {
    router.backstack.firstOrNull { it.tag == tag }?.let { remove(it.controller) }
}