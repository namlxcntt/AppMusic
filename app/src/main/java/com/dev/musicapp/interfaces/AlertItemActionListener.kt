
package com.dev.musicapp.interfaces

import com.dev.musicapp.customalertdialog.actions.AlertItemAction


interface AlertItemActionListener {
    fun onAlertItemClick(action: AlertItemAction)
}