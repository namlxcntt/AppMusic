

package com.dev.musicapp.customalertdialog.views.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.musicapp.R
import com.dev.musicapp.customalertdialog.actions.AlertItemAction
import com.dev.musicapp.customalertdialog.interfaces.ItemListener

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class DialogFragmentBase : BottomSheetDialogFragment(), ItemListener {

    protected lateinit var title: String
    protected lateinit var message: String
    protected lateinit var itemList: List<AlertItemAction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetAlertTheme)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.parent_dialog_layout, container, false)
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {}
}