package com.ivianuu.director.sample

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.ivianuu.director.Router
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.attachRouter
import com.ivianuu.director.handleBack
import com.ivianuu.director.hasRootController
import com.ivianuu.director.popController
import com.ivianuu.director.pushController
import com.ivianuu.director.sample.controller.ArchController
import com.ivianuu.director.sample.controller.HomeController
import com.ivianuu.director.sample.controller.ScopesController
import com.ivianuu.director.sample.controller.TravelerController
import com.ivianuu.director.sample.util.LoggingControllerFactory
import com.ivianuu.director.sample.util.LoggingLifecycleListener
import com.ivianuu.director.setRoot
import com.ivianuu.director.toTransaction
import kotlinx.android.synthetic.main.activity_main.controller_container
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : AppCompatActivity(), ActionBarProvider {

    private lateinit var router: Router

    override val providedActionBar: ActionBar
        get() = supportActionBar!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)

        router = attachRouter(
            controller_container,
            savedInstanceState,
            LoggingControllerFactory()
        ).apply {
            addLifecycleListener(LoggingLifecycleListener())

            if (!hasRootController) {
                setRoot(HomeController().toTransaction())
                pushController(RouterTransaction(TravelerController()))
                popController(backstack.last().controller)
                setRoot(RouterTransaction(ArchController()))
                setRoot(RouterTransaction(ScopesController()))
            }
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}