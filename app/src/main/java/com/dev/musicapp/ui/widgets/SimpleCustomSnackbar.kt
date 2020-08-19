package com.dev.musicapp.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.dev.musicapp.R
import com.dev.musicapp.extensions.findSuitableParent
import com.google.android.material.snackbar.BaseTransientBottomBar
import timber.log.Timber

class SimpleCustomSnackbar(
    parent: ViewGroup,
    content: SimpleCustomSnackbarView
) : BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {


    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            view: View,
            message: String, duration: Int,
            listener: View.OnClickListener?, icon: Int, action: String?, backgroundId: Int
        ): SimpleCustomSnackbar? {
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )
            return try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.layout_simple_custom_snackbar,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                customView.bind(message, listener, icon, action, backgroundId)

                SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }

    }

}