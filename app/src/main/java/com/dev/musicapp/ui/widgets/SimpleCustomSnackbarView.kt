package com.dev.musicapp.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.dev.musicapp.R
import com.dev.musicapp.databinding.ViewSnackbarSimpleBinding

import com.dev.musicapp.extensions.animateScale
import com.dev.musicapp.extensions.inflateWithBinding
import com.google.android.material.snackbar.ContentViewCallback


class SimpleCustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private var binding: ViewSnackbarSimpleBinding =
        inflateWithBinding(R.layout.view_snackbar_simple, true)

    init {
        clipToPadding = false
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        binding.icon.animateScale(0f, 1f, 500)
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }

    fun bind(
        message: String,
        listener: OnClickListener?,
        icon: Int,
        action: String?,
        backgroundId: Int
    ) {
        binding.apply {
            this.message.text = message
            action?.let {
                if (!it.isNullOrEmpty()) {
                    this.action.visibility = View.VISIBLE
                    this.action.text = it
                    this.action.setOnClickListener {
                        listener?.onClick(this.action)
                    }
                }
            }
            this.icon.setImageResource(icon)
            this.snackParent.setBackgroundResource(backgroundId)
        }
    }
}