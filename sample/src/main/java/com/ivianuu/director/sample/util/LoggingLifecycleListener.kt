package com.ivianuu.director.sample.util

import android.os.Bundle
import android.view.View
import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerChangeHandler
import com.ivianuu.director.ControllerChangeType
import com.ivianuu.director.ControllerLifecycleListener

/**
 * @author Manuel Wrage (IVIanuu)
 */
class LoggingLifecycleListener : ControllerLifecycleListener {

    private fun Controller.d(m: () -> String) {
        (this as Any).d { "Lifecycle: ${m.invoke()}" }
    }

    override fun preCreate(controller: Controller, savedInstanceState: Bundle?) {
        super.preCreate(controller, savedInstanceState)
        controller.d { "pre create" }
    }

    override fun postCreate(controller: Controller, savedInstanceState: Bundle?) {
        super.postCreate(controller, savedInstanceState)
        controller.d { "post create" }
    }

    override fun preInflateView(controller: Controller, savedViewState: Bundle?) {
        super.preInflateView(controller, savedViewState)
        controller.d { "pre inflate view" }
    }

    override fun postInflateView(controller: Controller, view: View, savedViewState: Bundle?) {
        super.postInflateView(controller, view, savedViewState)
        controller.d { "post inflate view" }
    }

    override fun preBindView(controller: Controller, view: View, savedViewState: Bundle?) {
        super.preBindView(controller, view, savedViewState)
        controller.d { "pre bind view" }
    }

    override fun postBindView(controller: Controller, view: View, savedViewState: Bundle?) {
        super.postBindView(controller, view, savedViewState)
        controller.d { "post bind view" }
    }

    override fun preAttach(controller: Controller, view: View) {
        super.preAttach(controller, view)
        controller.d { "pre attach" }
    }

    override fun postAttach(controller: Controller, view: View) {
        super.postAttach(controller, view)
        controller.d { "post attach" }
    }

    override fun preDetach(controller: Controller, view: View) {
        super.preDetach(controller, view)
        controller.d { "pre detach" }
    }

    override fun postDetach(controller: Controller, view: View) {
        super.postDetach(controller, view)
        controller.d { "post detach" }
    }

    override fun preUnbindView(controller: Controller, view: View) {
        super.preUnbindView(controller, view)
        controller.d { "pre unbind view" }
    }

    override fun postUnbindView(controller: Controller) {
        super.postUnbindView(controller)
        controller.d { "post unbind view" }
    }

    override fun preDestroy(controller: Controller) {
        super.preDestroy(controller)
        controller.d { "pre destroy" }
    }

    override fun postDestroy(controller: Controller) {
        super.postDestroy(controller)
        controller.d { "post destroy" }
    }

    override fun onChangeStart(
        controller: Controller,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        super.onChangeStart(controller, changeHandler, changeType)
        //  controller.d { "on change start -> $changeType, $changeHandler" }
    }

    override fun onChangeEnd(
        controller: Controller,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        super.onChangeEnd(controller, changeHandler, changeType)
        //  controller.d { "on change end -> $changeType, $changeHandler" }
    }

    override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
        super.onSaveInstanceState(controller, outState)
        controller.d { "on save instance state" }
    }

    override fun onSaveViewState(controller: Controller, outState: Bundle) {
        super.onSaveViewState(controller, outState)
        controller.d { "on save view state" }
    }
}