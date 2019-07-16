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

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.director.util.ActivityProxy
import com.ivianuu.director.util.CallState
import com.ivianuu.director.util.MockChangeHandler
import com.ivianuu.director.util.TestController
import com.ivianuu.director.util.defaultHandler
import com.ivianuu.director.util.listeningChangeHandler
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class ControllerLifecycleTest {

    private val activityProxy = ActivityProxy().create(null).start().resume()
    private val router = activityProxy.activity.router(activityProxy.view1).apply {
        if (!hasRoot) {
            setRoot(TestController().toTransaction())
        }
    }

    private val currentCallState = CallState()

    @Test
    fun testNormalLifecycle() {
        val controller = TestController()
        attachControllerListener(controller)

        val expectedCallState = CallState()
        assertCalls(expectedCallState, controller)

        router.push(
            controller
                .toTransaction()
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
                .popChangeHandler(getPopHandler(expectedCallState, controller))
        )

        assertCalls(expectedCallState, controller)

        router.popTop()

        assertCalls(expectedCallState, controller)
    }

    @Test
    fun testLifecycleWithActivityDestroy() {
        val controller = TestController()
        attachControllerListener(controller)

        val expectedCallState = CallState()

        assertCalls(expectedCallState, controller)
        router.push(
            controller
                .toTransaction()
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
        )

        assertCalls(expectedCallState, controller)

        activityProxy.pause()

        assertCalls(expectedCallState, controller)

        activityProxy.stop()

        expectedCallState.detachCalls++
        assertCalls(expectedCallState, controller)

        activityProxy.destroy()

        expectedCallState.destroyViewCalls++
        expectedCallState.destroyCalls++
        assertCalls(expectedCallState, controller)
    }

    @Test
    fun testLifecycleWithActivityBackground() {
        val controller = TestController()
        attachControllerListener(controller)

        val expectedCallState = CallState()

        assertCalls(expectedCallState, controller)
        router.push(
            controller
                .toTransaction()
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
        )

        assertCalls(expectedCallState, controller)

        activityProxy.pause()
        activityProxy.resume()

        assertCalls(expectedCallState, controller)
    }

    @Test
    fun testLifecycleCallOrder() {
        val testController = TestController()
        val callState = CallState()

        testController.addLifecycleListener(object : ControllerLifecycleListener {

            override fun preCreate(controller: Controller) {
                callState.createCalls++
                assertEquals(1, callState.createCalls)
                assertEquals(0, testController.currentCallState.createCalls)

                assertEquals(0, callState.createViewCalls)
                assertEquals(0, testController.currentCallState.createViewCalls)

                assertEquals(0, callState.attachCalls)
                assertEquals(0, testController.currentCallState.attachCalls)

                assertEquals(0, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun postCreate(controller: Controller) {
                callState.createCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(0, callState.createViewCalls)
                assertEquals(0, testController.currentCallState.createViewCalls)

                assertEquals(0, callState.attachCalls)
                assertEquals(0, testController.currentCallState.attachCalls)

                assertEquals(0, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun preCreateView(controller: Controller) {
                callState.createViewCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(1, callState.createViewCalls)
                assertEquals(0, testController.currentCallState.createViewCalls)

                assertEquals(0, callState.attachCalls)
                assertEquals(0, testController.currentCallState.attachCalls)

                assertEquals(0, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun postCreateView(
                controller: Controller,
                view: View
            ) {
                callState.createViewCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(0, callState.attachCalls)
                assertEquals(0, testController.currentCallState.attachCalls)

                assertEquals(0, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun preAttach(controller: Controller, view: View) {
                callState.attachCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(1, callState.attachCalls)
                assertEquals(0, testController.currentCallState.attachCalls)

                assertEquals(0, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun postAttach(controller: Controller, view: View) {
                callState.attachCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(0, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun preDetach(controller: Controller, view: View) {
                callState.detachCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(1, callState.detachCalls)
                assertEquals(0, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun postDetach(controller: Controller, view: View) {
                callState.detachCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(2, callState.detachCalls)
                assertEquals(1, testController.currentCallState.detachCalls)

                assertEquals(0, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun preDestroyView(controller: Controller, view: View) {
                callState.destroyViewCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(2, callState.detachCalls)
                assertEquals(1, testController.currentCallState.detachCalls)

                assertEquals(1, callState.destroyViewCalls)
                assertEquals(0, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun postDestroyView(controller: Controller) {
                callState.destroyViewCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(2, callState.detachCalls)
                assertEquals(1, testController.currentCallState.detachCalls)

                assertEquals(2, callState.destroyViewCalls)
                assertEquals(1, testController.currentCallState.destroyViewCalls)

                assertEquals(0, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun preDestroy(controller: Controller) {
                callState.destroyCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(2, callState.detachCalls)
                assertEquals(1, testController.currentCallState.detachCalls)

                assertEquals(2, callState.destroyViewCalls)
                assertEquals(1, testController.currentCallState.destroyViewCalls)

                assertEquals(1, callState.destroyCalls)
                assertEquals(0, testController.currentCallState.destroyCalls)
            }

            override fun postDestroy(controller: Controller) {
                callState.destroyCalls++
                assertEquals(2, callState.createCalls)
                assertEquals(1, testController.currentCallState.createCalls)

                assertEquals(2, callState.createViewCalls)
                assertEquals(1, testController.currentCallState.createViewCalls)

                assertEquals(2, callState.attachCalls)
                assertEquals(1, testController.currentCallState.attachCalls)

                assertEquals(2, callState.detachCalls)
                assertEquals(1, testController.currentCallState.detachCalls)

                assertEquals(2, callState.destroyViewCalls)
                assertEquals(1, testController.currentCallState.destroyViewCalls)

                assertEquals(2, callState.destroyCalls)
                assertEquals(1, testController.currentCallState.destroyCalls)
            }
        })

        router.push(
            testController
                .toTransaction()
                .changeHandler(defaultHandler())
        )

        router.popController(testController)

        assertEquals(2, callState.createCalls)
        assertEquals(2, callState.createViewCalls)
        assertEquals(2, callState.attachCalls)
        assertEquals(2, callState.detachCalls)
        assertEquals(2, callState.destroyViewCalls)
        assertEquals(2, callState.destroyCalls)
    }

    private fun getPushHandler(
        expectedCallState: CallState,
        controller: TestController
    ): MockChangeHandler {
        return listeningChangeHandler(object : MockChangeHandler.Listener {
            override fun willStartChange() {
                expectedCallState.createCalls++
                expectedCallState.createViewCalls++
                assertCalls(expectedCallState, controller)
            }

            override fun didAttachOrDetach() {
                expectedCallState.attachCalls++
                assertCalls(expectedCallState, controller)
            }

            override fun didEndChange() {
                assertCalls(expectedCallState, controller)
            }
        })
    }

    private fun getPopHandler(
        expectedCallState: CallState,
        controller: TestController
    ): MockChangeHandler {
        return listeningChangeHandler(object : MockChangeHandler.Listener {
            override fun willStartChange() {
                assertCalls(expectedCallState, controller)
            }

            override fun didAttachOrDetach() {
                expectedCallState.destroyViewCalls++
                expectedCallState.detachCalls++
                expectedCallState.destroyCalls++
                assertCalls(expectedCallState, controller)
            }

            override fun didEndChange() {
                assertCalls(expectedCallState, controller)
            }
        })
    }

    private fun assertCalls(callState: CallState, controller: TestController) {
        assertEquals(
            "Expected call counts and controller call counts do not match.",
            callState,
            controller.currentCallState
        )
        assertEquals(
            "Expected call counts and lifecycle call counts do not match.",
            callState,
            currentCallState
        )
    }

    private fun attachControllerListener(controller: Controller) {
        controller.addLifecycleListener(object : ControllerLifecycleListener {
            override fun postCreate(controller: Controller) {
                currentCallState.createCalls++
            }

            override fun postCreateView(
                controller: Controller,
                view: View
            ) {
                currentCallState.createViewCalls++
            }

            override fun postAttach(controller: Controller, view: View) {
                currentCallState.attachCalls++
            }

            override fun postDestroyView(controller: Controller) {
                super.postDestroyView(controller)
                currentCallState.destroyViewCalls++
            }

            override fun postDetach(controller: Controller, view: View) {
                currentCallState.detachCalls++
            }

            override fun postDestroy(controller: Controller) {
                currentCallState.destroyCalls++
            }

        })
    }

}