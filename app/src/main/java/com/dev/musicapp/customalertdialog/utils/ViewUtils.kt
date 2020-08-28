

package com.dev.musicapp.customalertdialog.utils

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape

object ViewUtils {
    /**
     * This method draws a round rect shape.
     * @param width: int
     * @param height: int
     * @param color: int
     * @return ShapeDrawable
     */
    fun drawRoundRectShape(
        width: Int,
        height: Int,
        color: Int,
        radius: Float = 30f
    ): ShapeDrawable {
        val r = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val oval = ShapeDrawable(RoundRectShape(r, RectF(), r))
        oval.intrinsicHeight = height
        oval.intrinsicWidth = width
        oval.paint.color = color
        return oval
    }

    fun dip2px(context: Context, dpValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}