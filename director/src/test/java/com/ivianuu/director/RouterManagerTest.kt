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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.director.util.ActivityProxy
import com.ivianuu.director.util.TestController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class RouterManagerTest {

    private val activityProxy = ActivityProxy().create(null).start().resume()
    private val manager = RouterManager(this)

    @Test
    fun testAddRemoveRouters() {
        val controller1 = TestController()
        val controller2 = TestController()

        assertEquals(0, manager.routers.size)

        val router1 =
            manager.getRouter(activityProxy.view1)
        val router2 =
            manager.getRouter(activityProxy.view2)

        router1.setRoot(controller1)
        router2.setRoot(controller2)

        assertEquals(2, manager.routers.size)
        assertEquals(router1, manager.routers[0])
        assertEquals(router2, manager.routers[1])
        assertEquals(1, router1.backstackSize)
        assertEquals(1, router2.backstackSize)
        assertEquals(controller1, router1.backstack.firstOrNull())
        assertEquals(controller2, router2.backstack.firstOrNull())

        manager.removeRouter(router2)

        assertEquals(1, manager.routers.size)
        assertEquals(router1, manager.routers[0])
        assertEquals(1, router1.backstackSize)
        assertEquals(0, router2.backstackSize)
        assertEquals(controller1, router1.backstack.firstOrNull())

        manager.removeRouter(router1)

        assertEquals(0, manager.routers.size)
        assertEquals(0, router1.backstackSize)
        assertEquals(0, router2.backstackSize)
    }

    @Test
    fun testRestoredRouterBackstack() {
        val transaction1 = TestController()
        val transaction2 = TestController()

        var router = manager.getRouter(activityProxy.view1)
            .apply { popsLastView = true }

        router.setRoot(transaction1)
        router.push(transaction2)

        val savedState = router.saveInstanceState()
        manager.removeRouter(router)

        router = manager.getRouter(activityProxy.view1)
        assertEquals(0, router.backstackSize)

        router.restoreInstanceState(savedState)

        assertEquals(2, router.backstackSize)

        val restoredChildController1 = router.backstack.first()
        val restoredChildController2 = router.backstack[1]

        assertEquals(
            transaction1.transactionIndex,
            restoredChildController1.transactionIndex
        )
        assertEquals(
            transaction1.instanceId,
            restoredChildController1.instanceId
        )

        assertEquals(
            transaction2.transactionIndex,
            restoredChildController2.transactionIndex
        )
        assertEquals(
            transaction2.instanceId,
            restoredChildController2.instanceId
        )

        assertTrue(router.handleBack())
        assertEquals(1, router.backstackSize)
        assertEquals(restoredChildController1, router.backstack[0])

        assertTrue(router.handleBack())
        assertEquals(0, router.backstackSize)
    }

}