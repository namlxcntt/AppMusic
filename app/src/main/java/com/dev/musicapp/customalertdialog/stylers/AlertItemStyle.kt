
package com.dev.musicapp.customalertdialog.stylers

import android.graphics.Color
import com.dev.musicapp.customalertdialog.stylers.base.ItemStyle
class AlertItemStyle(
    var backgroundColor: Int = Color.parseColor("#F8F8F8"),
    var selectedBackgroundColor: Int = Color.parseColor("#E8E8E8"),
    var textColor: Int = Color.parseColor("#131313"),
    var selectedTextColor: Int = Color.parseColor("#F44336"),
    var cornerRadius: Float = 60f
) : ItemStyle()