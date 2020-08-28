

package com.dev.musicapp.customalertdialog.extensions

import androidx.fragment.app.Fragment

fun Fragment.addOnWindowFocusChangeListener(callback: (hasFocus: Boolean) -> Unit) =
    view?.viewTreeObserver?.addOnWindowFocusChangeListener { callback.invoke(it) }