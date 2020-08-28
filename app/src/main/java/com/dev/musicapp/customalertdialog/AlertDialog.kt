
package com.dev.musicapp.customalertdialog

import androidx.appcompat.app.AppCompatActivity
import com.dev.musicapp.customalertdialog.enums.AlertType
import com.dev.musicapp.customalertdialog.actions.AlertItemAction
import com.dev.musicapp.customalertdialog.stylers.AlertItemStyle
import com.dev.musicapp.customalertdialog.stylers.base.ItemStyle
import com.dev.musicapp.customalertdialog.views.BottomSheetDialogAlert
import com.dev.musicapp.customalertdialog.views.DialogAlert
import com.dev.musicapp.customalertdialog.views.InputDialog
import com.dev.musicapp.customalertdialog.views.base.DialogFragmentBase

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