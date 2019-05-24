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
class LoggingControllerLifecycleListener : ControllerLifecycleListener {

    private fun Controller.d(m: () -> String) {
        (this as Any).d { "Lifecycle: ${m.invoke()}" }
    }

    override fun preCreate(controller: Controller, savedInstanceState: Bundle?) {
        controller.d { "pre create" }
    }

    override fun postCreate(controller: Controller, savedInstanceState: Bundle?) {
        controller.d { "post create" }
    }

    override fun preCreateView(controller: Controller, savedViewState: Bundle?) {
        controller.d { "pre create view" }
    }

    override fun postCreateView(controller: Controller, view: View, savedViewState: Bundle?) {
        controller.d { "post create view" }
    }

    override fun preAttach(controller: Controller, view: View) {
        controller.d { "pre attach" }
    }

    override fun postAttach(controller: Controller, view: View) {
        controller.d { "post attach" }
    }

    override fun preDetach(controller: Controller, view: View) {
        controller.d { "pre detach" }
    }

    override fun postDetach(controller: Controller, view: View) {
        controller.d { "post detach" }
    }

    override fun preDestroyView(controller: Controller, view: View) {
        controller.d { "pre destroy view" }
    }

    override fun postDestroyView(controller: Controller) {
        controller.d { "post destroy view" }
    }

    override fun preDestroy(controller: Controller) {
        controller.d { "pre destroy" }
    }

    override fun postDestroy(controller: Controller) {
        controller.d { "post destroy" }
    }

    override fun onRestoreInstanceState(controller: Controller, savedInstanceState: Bundle) {
        controller.d { "on restore instance state" }
    }

    override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
        controller.d { "on save instance state" }
    }

    override fun onRestoreViewState(controller: Controller, view: View, savedViewState: Bundle) {
        controller.d { "on restore view state" }
    }

    override fun onSaveViewState(controller: Controller, view: View, outState: Bundle) {
        controller.d { "on save view state" }
    }

    override fun onChangeStarted(
        controller: Controller,
        other: Controller?,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        super.onChangeStarted(controller, other, changeHandler, changeType)
        controller.d { "on change started" }
    }

    override fun onChangeEnded(
        controller: Controller,
        other: Controller?,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        super.onChangeEnded(controller, other, changeHandler, changeType)
        controller.d { "on change ended" }
    }
}