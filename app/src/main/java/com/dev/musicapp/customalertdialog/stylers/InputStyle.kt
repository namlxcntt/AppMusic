

package com.dev.musicapp.customalertdialog.stylers

import android.graphics.Color
import com.dev.musicapp.customalertdialog.stylers.base.ItemStyle

class InputStyle(
    var backgroundColor: Int = Color.parseColor("#131313"),
    val inputColor: Int = Color.parseColor("#434343"),
    val textColor: Int = Color.parseColor("#FFFFFF"),
    val hintTextColor: Int = Color.parseColor("#AAAAAA"),
    val acceptColor: Int = Color.parseColor("#00FF00"),
    var text: String = "",
    var cornerRadius: Float = 60f
) : ItemStyle()