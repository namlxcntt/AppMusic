

package com.dev.musicapp.customalertdialog.actions

import android.view.View
import androidx.annotation.DrawableRes
import com.dev.musicapp.customalertdialog.enums.AlertItemTheme

data class AlertItemAction(
    val title: String = "",
    var selected: Boolean = false,
    val theme: AlertItemTheme? = AlertItemTheme.DEFAULT,
    @DrawableRes val icon: Int = -1,
    var input: String = "",
    var root: View? = null,
    var action: (AlertItemAction) -> Unit = {}
)