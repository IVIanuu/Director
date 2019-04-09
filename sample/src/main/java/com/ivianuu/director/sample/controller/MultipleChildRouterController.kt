package com.ivianuu.director.sample.controller

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.ivianuu.director.getChildRouter
import com.ivianuu.director.hasRoot
import com.ivianuu.director.push
import com.ivianuu.director.sample.R
import com.ivianuu.director.setRoot


class MultipleChildRouterController : BaseController() {

    override val layoutRes get() = R.layout.controller_multiple_child_routers
    override val toolbarTitle: String?
        get() = "Child Router Demo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listOf(R.id.container_0, R.id.container_1, R.id.container_2)
            .map { getChildRouter(it) }
            .filter { !it.hasRoot }
            .forEach {
                it.setRoot(
                    NavigationController.newInstance(
                        0,
                        NavigationController.DisplayUpMode.HIDE,
                        false
                    )
                )
            }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        Handler().postDelayed(Runnable { router.push(HomeController()) }, 1000)
    }
}