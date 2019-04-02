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

import com.ivianuu.director.ControllerFactory
import com.ivianuu.director.ReflectiveControllerFactory

class LoggingControllerFactory : ControllerFactory {

    private val wrapped = ReflectiveControllerFactory()

    override fun createController(
        classLoader: ClassLoader,
        className: String
    ) = wrapped.createController(classLoader, className)
        .also { d { "instantiated $it for $className" } }
}