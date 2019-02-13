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

package com.ivianuu.director.sample.app

import android.app.Application
import com.ivianuu.director.DirectorPlugins
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration
import com.ivianuu.director.common.changehandler.defaultTransitionDuration
import com.ivianuu.director.defaultBlockBackClicksOnTransactions
import com.ivianuu.director.defaultBlockTouchesOnTransactions
import com.ivianuu.director.defaultControllerFactory
import com.ivianuu.director.sample.util.LoggingControllerFactory
import com.ivianuu.director.setDefaultHandler
import com.squareup.leakcanary.LeakCanary

/**
 * @author Manuel Wrage (IVIanuu)
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        //LeakCanary.get(this)

        // apply global config
        DirectorPlugins.defaultControllerFactory = LoggingControllerFactory()
        DirectorPlugins.defaultBlockBackClicksOnTransactions = true
        DirectorPlugins.defaultBlockTouchesOnTransactions = true
        DirectorPlugins.defaultAnimationDuration = 220
        DirectorPlugins.defaultTransitionDuration = 220
        DirectorPlugins.setDefaultHandler(FadeChangeHandler())
    }

}