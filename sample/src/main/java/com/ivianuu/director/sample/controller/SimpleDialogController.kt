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

package com.ivianuu.director.sample.controller

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ivianuu.director.activity
import com.ivianuu.director.dialog.DialogController


/**
 * @author Manuel Wrage (IVIanuu)
 */
class SimpleDialogController : DialogController() {

    override fun onCreateDialog(savedViewState: Bundle?): Dialog = AlertDialog.Builder(activity)
        .setTitle("Hello")
        .setMessage("This is a simple dialog controller.")
        .setCancelable(isCancelable)
        .setPositiveButton("OK") { _, _ ->
            Toast.makeText(activity, "Ok clicked!", Toast.LENGTH_SHORT).show()
        }
        .setNegativeButton("Cancel") { _, _  -> dismiss() }
        .create()

}