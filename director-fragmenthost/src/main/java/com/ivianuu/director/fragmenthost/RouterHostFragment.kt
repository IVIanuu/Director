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

package com.ivianuu.director.fragmenthost

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ivianuu.director.ControllerFactory
import com.ivianuu.director.Router
import com.ivianuu.director.RouterDelegate

class RouterHostFragment : Fragment() {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { RouterDelegate(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        delegate.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    internal fun getRouter(
        container: ViewGroup,
        controllerFactory: ControllerFactory?
    ): Router = delegate.getRouter(container, controllerFactory)

    companion object {
        private const val FRAGMENT_TAG = "com.ivianuu.director.fragmenthost.RouterHostFragment"

        internal fun install(fm: FragmentManager): RouterHostFragment {
            return (findInFragmentManager(fm) ?: RouterHostFragment().also {
                fm.beginTransaction()
                    .add(it, FRAGMENT_TAG)
                    .commitNow()
            })
        }

        private fun findInFragmentManager(fm: FragmentManager): RouterHostFragment? =
            fm.findFragmentByTag(FRAGMENT_TAG) as? RouterHostFragment
    }

}