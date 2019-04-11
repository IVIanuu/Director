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

import android.os.Bundle
import android.view.View
import com.ivianuu.closeable.Closeable

/**
 * Listener for [Controller]s
 */
interface ControllerListener {

    fun preCreate(controller: Controller, savedInstanceState: Bundle?) {
    }

    fun postCreate(controller: Controller, savedInstanceState: Bundle?) {
    }

    fun preCreateView(controller: Controller, savedViewState: Bundle?) {
    }

    fun postCreateView(controller: Controller, view: View, savedViewState: Bundle?) {
    }

    fun preAttach(controller: Controller, view: View) {
    }

    fun postAttach(controller: Controller, view: View) {
    }

    fun preDetach(controller: Controller, view: View) {
    }

    fun postDetach(controller: Controller, view: View) {
    }

    fun preDestroyView(controller: Controller, view: View) {
    }

    fun postDestroyView(controller: Controller) {
    }

    fun preDestroy(controller: Controller) {
    }

    fun postDestroy(controller: Controller) {
    }

    fun onRestoreInstanceState(controller: Controller, savedInstanceState: Bundle) {
    }

    fun onSaveInstanceState(controller: Controller, outState: Bundle) {
    }

    fun onRestoreViewState(controller: Controller, view: View, savedViewState: Bundle) {
    }

    fun onSaveViewState(controller: Controller, view: View, outState: Bundle) {
    }

    fun onChangeStarted(
        controller: Controller,
        other: Controller?,
        changeHandler: ChangeHandler,
        changeType: ControllerChangeType
    ) {
    }

    fun onChangeEnded(
        controller: Controller,
        other: Controller?,
        changeHandler: ChangeHandler,
        changeType: ControllerChangeType
    ) {
    }
}

/**
 * Returns a new [ControllerListener]
 */
fun ControllerListener(
    preCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    postCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    preCreateView: ((controller: Controller, savedViewState: Bundle?) -> Unit)? = null,
    postCreateView: ((controller: Controller, view: View, savedViewState: Bundle?) -> Unit)? = null,
    preAttach: ((controller: Controller, view: View) -> Unit)? = null,
    postAttach: ((controller: Controller, view: View) -> Unit)? = null,
    preDetach: ((controller: Controller, view: View) -> Unit)? = null,
    postDetach: ((controller: Controller, view: View) -> Unit)? = null,
    preDestroyView: ((controller: Controller, view: View) -> Unit)? = null,
    postDestroyView: ((controller: Controller) -> Unit)? = null,
    preDestroy: ((controller: Controller) -> Unit)? = null,
    postDestroy: ((controller: Controller) -> Unit)? = null,
    onRestoreInstanceState: ((controller: Controller, savedInstanceState: Bundle) -> Unit)? = null,
    onSaveInstanceState: ((controller: Controller, outState: Bundle) -> Unit)? = null,
    onRestoreViewState: ((controller: Controller, view: View, savedViewState: Bundle) -> Unit)? = null,
    onSaveViewState: ((controller: Controller, view: View, outState: Bundle) -> Unit)? = null,
    onChangeStarted: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null,
    onChangeEnded: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null
): ControllerListener = LambdaControllerListener(
    preCreate = preCreate, postCreate = postCreate,
    preCreateView = preCreateView, postCreateView = postCreateView,
    preAttach = preAttach, postAttach = postAttach,
    preDetach = preDetach, postDetach = postDetach,
    preDestroyView = preDestroyView, postDestroyView = postDestroyView,
    preDestroy = preDestroy, postDestroy = postDestroy,
    onRestoreInstanceState = onRestoreInstanceState, onSaveInstanceState = onSaveInstanceState,
    onRestoreViewState = onRestoreViewState, onSaveViewState = onSaveViewState,
    onChangeStarted = onChangeStarted, onChangeEnded = onChangeEnded
)

fun Controller.doOnPreCreate(block: (controller: Controller, savedInstanceState: Bundle?) -> Unit): Closeable =
    addListener(preCreate = block)

fun Controller.doOnPostCreate(block: (controller: Controller, savedInstanceState: Bundle?) -> Unit): Closeable =
    addListener(postCreate = block)

fun Controller.doOnPreCreateView(block: (controller: Controller, savedViewState: Bundle?) -> Unit): Closeable =
    addListener(preCreateView = block)

fun Controller.doOnPostCreateView(block: (controller: Controller, view: View, savedViewState: Bundle?) -> Unit): Closeable =
    addListener(postCreateView = block)

fun Controller.doOnPreAttach(block: (controller: Controller, view: View) -> Unit): Closeable =
    addListener(preAttach = block)

fun Controller.doOnPostAttach(block: (controller: Controller, view: View) -> Unit): Closeable =
    addListener(postAttach = block)

fun Controller.doOnPreDetach(block: (controller: Controller, view: View) -> Unit): Closeable =
    addListener(preDetach = block)

fun Controller.doOnPostDetach(block: (controller: Controller, view: View) -> Unit): Closeable =
    addListener(postDetach = block)

fun Controller.doOnPreDestroyView(block: (controller: Controller, view: View) -> Unit): Closeable =
    addListener(preDestroyView = block)

fun Controller.doOnPostDestroyView(block: (controller: Controller) -> Unit): Closeable =
    addListener(postDestroyView = block)

fun Controller.doOnPreDestroy(block: (controller: Controller) -> Unit): Closeable =
    addListener(preDestroy = block)

fun Controller.doOnPostDestroy(block: (controller: Controller) -> Unit): Closeable =
    addListener(postDestroy = block)

fun Controller.doOnRestoreInstanceState(block: (controller: Controller, savedInstanceState: Bundle) -> Unit): Closeable =
    addListener(onRestoreInstanceState = block)

fun Controller.doOnSaveInstanceState(block: (controller: Controller, outState: Bundle) -> Unit): Closeable =
    addListener(onSaveInstanceState = block)

fun Controller.doOnRestoreViewState(block: (controller: Controller, view: View, savedViewState: Bundle) -> Unit): Closeable =
    addListener(onRestoreViewState = block)

fun Controller.doOnSaveViewState(block: (controller: Controller, view: View, outState: Bundle) -> Unit): Closeable =
    addListener(onSaveViewState = block)

fun Controller.doOnChangeStart(block: (controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit): Closeable =
    addListener(onChangeStarted = block)

fun Controller.doOnChangeEnd(block: (controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit): Closeable =
    addListener(onChangeEnded = block)

fun Controller.addListener(
    preCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    postCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    preCreateView: ((controller: Controller, savedViewState: Bundle?) -> Unit)? = null,
    postCreateView: ((controller: Controller, view: View, savedViewState: Bundle?) -> Unit)? = null,
    preAttach: ((controller: Controller, view: View) -> Unit)? = null,
    postAttach: ((controller: Controller, view: View) -> Unit)? = null,
    preDetach: ((controller: Controller, view: View) -> Unit)? = null,
    postDetach: ((controller: Controller, view: View) -> Unit)? = null,
    preDestroyView: ((controller: Controller, view: View) -> Unit)? = null,
    postDestroyView: ((controller: Controller) -> Unit)? = null,
    preDestroy: ((controller: Controller) -> Unit)? = null,
    postDestroy: ((controller: Controller) -> Unit)? = null,
    onRestoreInstanceState: ((controller: Controller, savedInstanceState: Bundle) -> Unit)? = null,
    onSaveInstanceState: ((controller: Controller, outState: Bundle) -> Unit)? = null,
    onRestoreViewState: ((controller: Controller, view: View, savedViewState: Bundle) -> Unit)? = null,
    onSaveViewState: ((controller: Controller, view: View, outState: Bundle) -> Unit)? = null,
    onChangeStarted: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null,
    onChangeEnded: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null
): Closeable = ControllerListener(
    preCreate = preCreate, postCreate = postCreate,
    preCreateView = preCreateView, postCreateView = postCreateView,
    preAttach = preAttach, postAttach = postAttach,
    preDetach = preDetach, postDetach = postDetach,
    preDestroyView = preDestroyView, postDestroyView = postDestroyView,
    preDestroy = preDestroy, postDestroy = postDestroy,
    onRestoreInstanceState = onRestoreInstanceState, onSaveInstanceState = onSaveInstanceState,
    onRestoreViewState = onRestoreViewState, onSaveViewState = onSaveViewState,
    onChangeStarted = onChangeStarted, onChangeEnded = onChangeEnded
).let { addListener(it) }

fun Router.addControllerListener(
    recursive: Boolean = false,
    preCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    postCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    preCreateView: ((controller: Controller, savedViewState: Bundle?) -> Unit)? = null,
    postCreateView: ((controller: Controller, view: View, savedViewState: Bundle?) -> Unit)? = null,
    preAttach: ((controller: Controller, view: View) -> Unit)? = null,
    postAttach: ((controller: Controller, view: View) -> Unit)? = null,
    preDetach: ((controller: Controller, view: View) -> Unit)? = null,
    postDetach: ((controller: Controller, view: View) -> Unit)? = null,
    preDestroyView: ((controller: Controller, view: View) -> Unit)? = null,
    postDestroyView: ((controller: Controller) -> Unit)? = null,
    preDestroy: ((controller: Controller) -> Unit)? = null,
    postDestroy: ((controller: Controller) -> Unit)? = null,
    onRestoreInstanceState: ((controller: Controller, savedInstanceState: Bundle) -> Unit)? = null,
    onSaveInstanceState: ((controller: Controller, outState: Bundle) -> Unit)? = null,
    onRestoreViewState: ((controller: Controller, view: View, savedViewState: Bundle) -> Unit)? = null,
    onSaveViewState: ((controller: Controller, view: View, outState: Bundle) -> Unit)? = null,
    onChangeStarted: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null,
    onChangeEnded: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null
): Closeable {
    return addControllerListener(
        ControllerListener(
            preCreate = preCreate,
            postCreate = postCreate,
            preCreateView = preCreateView,
            postCreateView = postCreateView,
            preAttach = preAttach,
            postAttach = postAttach,
            preDetach = preDetach,
            postDetach = postDetach,
            preDestroyView = preDestroyView,
            postDestroyView = postDestroyView,
            preDestroy = preDestroy,
            postDestroy = postDestroy,
            onRestoreInstanceState = onRestoreInstanceState,
            onSaveInstanceState = onSaveInstanceState,
            onRestoreViewState = onRestoreViewState,
            onSaveViewState = onSaveViewState,
            onChangeStarted = onChangeStarted,
            onChangeEnded = onChangeEnded
        ),

        recursive = recursive
    )
}

private class LambdaControllerListener(
    private val preCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    private val postCreate: ((controller: Controller, savedInstanceState: Bundle?) -> Unit)? = null,
    private val preCreateView: ((controller: Controller, savedViewState: Bundle?) -> Unit)? = null,
    private val postCreateView: ((controller: Controller, view: View, savedViewState: Bundle?) -> Unit)? = null,
    private val preAttach: ((controller: Controller, view: View) -> Unit)? = null,
    private val postAttach: ((controller: Controller, view: View) -> Unit)? = null,
    private val preDetach: ((controller: Controller, view: View) -> Unit)? = null,
    private val postDetach: ((controller: Controller, view: View) -> Unit)? = null,
    private val preDestroyView: ((controller: Controller, view: View) -> Unit)? = null,
    private val postDestroyView: ((controller: Controller) -> Unit)? = null,
    private val preDestroy: ((controller: Controller) -> Unit)? = null,
    private val postDestroy: ((controller: Controller) -> Unit)? = null,
    private val onRestoreInstanceState: ((controller: Controller, savedInstanceState: Bundle) -> Unit)? = null,
    private val onSaveInstanceState: ((controller: Controller, outState: Bundle) -> Unit)? = null,
    private val onRestoreViewState: ((controller: Controller, view: View, savedViewState: Bundle) -> Unit)? = null,
    private val onSaveViewState: ((controller: Controller, view: View, outState: Bundle) -> Unit)? = null,
    private val onChangeStarted: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null,
    private val onChangeEnded: ((controller: Controller, other: Controller?, changeHandler: ChangeHandler, changeType: ControllerChangeType) -> Unit)? = null
) : ControllerListener {
    override fun preCreate(controller: Controller, savedInstanceState: Bundle?) {
        preCreate?.invoke(controller, savedInstanceState)
    }

    override fun postCreate(controller: Controller, savedInstanceState: Bundle?) {
        postCreate?.invoke(controller, savedInstanceState)
    }

    override fun preCreateView(controller: Controller, savedViewState: Bundle?) {
        preCreateView?.invoke(controller, savedViewState)
    }

    override fun postCreateView(controller: Controller, view: View, savedViewState: Bundle?) {
        postCreateView?.invoke(controller, view, savedViewState)
    }

    override fun preAttach(controller: Controller, view: View) {
        preAttach?.invoke(controller, view)
    }

    override fun postAttach(controller: Controller, view: View) {
        postAttach?.invoke(controller, view)
    }

    override fun preDetach(controller: Controller, view: View) {
        preDetach?.invoke(controller, view)
    }

    override fun postDetach(controller: Controller, view: View) {
        postDetach?.invoke(controller, view)
    }

    override fun preDestroyView(controller: Controller, view: View) {
        preDestroyView?.invoke(controller, view)
    }

    override fun postDestroyView(controller: Controller) {
        postDestroyView?.invoke(controller)
    }

    override fun preDestroy(controller: Controller) {
        preDestroy?.invoke(controller)
    }

    override fun postDestroy(controller: Controller) {
        postDestroy?.invoke(controller)
    }

    override fun onRestoreInstanceState(controller: Controller, savedInstanceState: Bundle) {
        onRestoreInstanceState?.invoke(controller, savedInstanceState)
    }

    override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
        onSaveInstanceState?.invoke(controller, outState)
    }

    override fun onRestoreViewState(controller: Controller, view: View, savedViewState: Bundle) {
        onRestoreViewState?.invoke(controller, view, savedViewState)
    }

    override fun onSaveViewState(controller: Controller, view: View, outState: Bundle) {
        onSaveViewState?.invoke(controller, view, outState)
    }

    override fun onChangeStarted(
        controller: Controller,
        other: Controller?,
        changeHandler: ChangeHandler,
        changeType: ControllerChangeType
    ) {
        onChangeStarted?.invoke(controller, other, changeHandler, changeType)
    }

    override fun onChangeEnded(
        controller: Controller,
        other: Controller?,
        changeHandler: ChangeHandler,
        changeType: ControllerChangeType
    ) {
        onChangeEnded?.invoke(controller, other, changeHandler, changeType)
    }
}