

package com.dev.musicapp.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding> LayoutInflater.inflateWithBinding(
    @LayoutRes layoutRes: Int,
    parent: ViewGroup?,
    attachToRoot: Boolean = false
): T {
    return DataBindingUtil.inflate(this, layoutRes, parent, attachToRoot) as T
}