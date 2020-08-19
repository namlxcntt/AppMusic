/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dev.musicapp.alertdialog

import androidx.appcompat.app.AppCompatActivity
import com.dev.musicapp.alertdialog.enums.AlertType
import com.dev.musicapp.alertdialog.actions.AlertItemAction
import com.dev.musicapp.alertdialog.stylers.AlertItemStyle
import com.dev.musicapp.alertdialog.stylers.base.ItemStyle
import com.dev.musicapp.alertdialog.views.BottomSheetDialogAlert
import com.dev.musicapp.alertdialog.views.DialogAlert
import com.dev.musicapp.alertdialog.views.InputDialog
import com.dev.musicapp.alertdialog.views.base.DialogFragmentBase

class AlertDialog(
    private val title: String,
    private val message: String,
    private var style: ItemStyle,
    private val type: AlertType,
    private val inputText: String = ""
) {

    private var theme: AlertType? = AlertType.DIALOG
    private val actions: ArrayList<AlertItemAction> = ArrayList()
    private var alert: DialogFragmentBase? = null

    /**
     * Add Item to AlertDialog
     * If you are using InputDialog, you can only add 2 actions
     * that will appear at the dialog bottom
     * @param item: AlertItemAction
     */
    fun addItem(item: AlertItemAction) {
        actions.add(item)
    }

    /**
     * Receives an Activity (AppCompatActivity), It's is necessary to getContext and show AlertDialog
     * @param activity: AppCompatActivity
     */
    fun show(activity: AppCompatActivity) {
        alert = when (type) {
            AlertType.BOTTOM_SHEET -> BottomSheetDialogAlert.newInstance(title, message, actions, style)
            AlertType.DIALOG -> DialogAlert.newInstance(title, message, actions, style)
            AlertType.INPUT -> InputDialog.newInstance(title, message, actions, style, inputText)
        }
        alert?.show(activity.supportFragmentManager, alert?.tag)
    }

    /**
     * Set type for alert. Choose between "AlertType.DIALOG" and "AlertType.BOTTOM_SHEET"
     * @param type: AlertType
     */
    fun setType(type: AlertType) {
        this.theme = type
    }

    /**
     * Update all style in the application
     * @param style: AlertType
     */
    fun setStyle(style: AlertItemStyle) {
        this.style = style
    }

    /**
     * Get the style
     * @return style: AlertItemStyle
     */
    fun getStyle(): ItemStyle {
        return this.style
    }
}