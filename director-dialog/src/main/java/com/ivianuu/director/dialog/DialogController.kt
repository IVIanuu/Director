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

package com.ivianuu.director.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.ivianuu.director.Controller
import com.ivianuu.director.Router
import com.ivianuu.director.SimpleSwapChangeHandler
import com.ivianuu.director.activity
import com.ivianuu.director.changeHandler
import com.ivianuu.director.pop
import com.ivianuu.director.push
import com.ivianuu.director.tag
import com.ivianuu.director.toTransaction

/**
 * A controller counterpart for dialog fragments
 */
abstract class DialogController : Controller(),
    DialogInterface.OnShowListener, DialogInterface.OnCancelListener,
    DialogInterface.OnDismissListener {

    var style = STYLE_NORMAL
        private set

    var theme = 0
        private set

    /**
     * Whether or the dialog should be cancelable
     */
    var isCancelable = true
        set(value) {
            field = value
            dialog?.setCancelable(value)
        }

    /**
     * The dialog of this controller
     */
    var dialog: Dialog? = null
        private set

    /**
     * Whether or not this controller is dismissed
     */
    var isDismissed = false
        private set

    private var dialogView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            style = savedInstanceState.getInt(KEY_STYLE, STYLE_NORMAL)
            theme = savedInstanceState.getInt(KEY_THEME, 0)
            isCancelable = savedInstanceState.getBoolean(KEY_CANCELABLE, true)
            isDismissed = savedInstanceState.getBoolean(KEY_DISMISSED, isDismissed)
        }
    }

    override fun onBuildView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = View(inflater.context)

        val dialog = onBuildDialog(savedViewState).also { this.dialog = it }

        isDismissed = false

        when (style) {
            STYLE_NO_INPUT -> {
                dialog.window!!.addFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            }
            // fall through...
            STYLE_NO_FRAME, STYLE_NO_TITLE -> dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        val dialogView = onBuildDialogView(
            LayoutInflater.from(dialog.context),
            savedViewState
        ).also { this.dialogView = it }

        dialogView?.let(dialog::setContentView)

        dialog.ownerActivity = activity
        dialog.setCancelable(isCancelable)
        dialog.setOnShowListener(this)
        dialog.setOnCancelListener(this)
        dialog.setOnDismissListener(this)

        savedViewState?.getBundle(KEY_DIALOG_STATE)?.let(dialog::onRestoreInstanceState)

        return view
    }

    protected open fun onBuildDialog(savedViewState: Bundle?): Dialog = Dialog(activity, theme)

    protected open fun onBuildDialogView(
        inflater: LayoutInflater,
        savedViewState: Bundle?
    ): View? = null

    override fun onAttach(view: View) {
        super.onAttach(view)
        dialog?.show()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        dialog?.hide()
    }

    override fun onUnbindView(view: View) {
        super.onUnbindView(view)
        dialog?.let {
            it.setOnShowListener(null)
            it.setOnCancelListener(null)
            it.setOnDismissListener(null)
            it.dismiss()
        }
        dialog = null
        isDismissed = true
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        dialog?.onSaveInstanceState()?.let { outState.putBundle(KEY_DIALOG_STATE, it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_STYLE, style)
        outState.putInt(KEY_THEME, theme)
        outState.putBoolean(KEY_CANCELABLE, isCancelable)
        outState.putBoolean(KEY_DISMISSED, isDismissed)
    }

    override fun onShow(dialogInterface: DialogInterface) {
    }

    override fun onCancel(dialog: DialogInterface) {
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismiss()
    }

    fun setStyle(style: Int, theme: Int) {
        this.style = style
        if (this.style == STYLE_NO_FRAME || this.style == STYLE_NO_INPUT) {
            this.theme = android.R.style.Theme_Panel
        }
        if (theme != 0) {
            this.theme = theme
        }
    }

    /**
     * Dismiss the dialog and pop this controller
     */
    open fun dismiss() {
        if (isDismissed) return

        dialog?.dismiss()
        isDismissed = true

        router.pop(this)
    }

    companion object {
        const val STYLE_NORMAL = 0
        const val STYLE_NO_TITLE = 1
        const val STYLE_NO_FRAME = 2
        const val STYLE_NO_INPUT = 3

        private const val KEY_DIALOG_STATE = "DialogController.dialogState"
        private const val KEY_STYLE = "DialogController.style"
        private const val KEY_THEME = "DialogController.theme"
        private const val KEY_CANCELABLE = "DialogController.cancelable"
        private const val KEY_DISMISSED = "DialogController.dismissed"
    }
}

fun DialogController.show(router: Router, tag: String? = null) {
    router.push(
        toTransaction()
            .changeHandler(SimpleSwapChangeHandler(removesFromViewOnPush = false))
            .tag(tag)
    )
}
