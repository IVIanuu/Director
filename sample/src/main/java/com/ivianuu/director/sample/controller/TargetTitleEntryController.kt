package com.ivianuu.director.sample.controller

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ivianuu.director.Controller
import com.ivianuu.director.sample.R
import kotlinx.android.synthetic.main.controller_target_title_entry.*

class TargetTitleEntryController : BaseController() {

    override var title: String?
        get() = "Target Controller Demo"
        set(value) {
            super.title = value
        }

    interface TargetTitleEntryControllerListener {
        fun onTitlePicked(option: String)
    }

    override val layoutRes = R.layout.controller_target_title_entry

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        btn_use_title.setOnClickListener {
            (targetController as? TargetTitleEntryControllerListener)
                ?.onTitlePicked(edit_text.text.toString())
            router.popController(this)
        }
    }

    override fun onDetach(view: View) {
        val imm =
            edit_text.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edit_text!!.windowToken, 0)
    }

    companion object {

        fun <T> newInstance(targetController: T)
                where T : Controller, T : TargetTitleEntryControllerListener = TargetTitleEntryController().apply {
            this.targetController = targetController
        }

    }
}