
package com.dev.musicapp.customalertdialog.interfaces

import android.view.View
import com.dev.musicapp.customalertdialog.actions.AlertItemAction

interface ItemListener {
    /**
     * This method sets the views style
     * @param view: View
     * @param alertItemAction: AlertItemAction
     */
    fun updateItem(view: View, alertItemAction: AlertItemAction)
}