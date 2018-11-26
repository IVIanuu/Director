package com.ivianuu.director.sample.controller

import android.view.View
import com.ivianuu.director.sample.R
import com.ivianuu.director.sample.util.bundleOf
import kotlinx.android.synthetic.main.controller_text.text_view

class TextController : BaseController() {

    override val layoutRes get() = R.layout.controller_text

    override fun onBindView(view: View) {
        super.onBindView(view)
        text_view.text = args.getString(KEY_TEXT)
    }

    companion object {
        private const val KEY_TEXT = "TextController.text"

        fun newInstance(text: String) = TextController().apply {
            args = bundleOf(KEY_TEXT to text)
        }
    }
}
