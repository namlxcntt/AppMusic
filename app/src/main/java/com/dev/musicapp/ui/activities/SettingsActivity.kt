package com.dev.musicapp.ui.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dev.musicapp.R
import com.dev.musicapp.customalertdialog.AlertDialog
import com.dev.musicapp.customalertdialog.actions.AlertItemAction
import com.dev.musicapp.customalertdialog.enums.AlertItemTheme
import com.dev.musicapp.customalertdialog.enums.AlertType
import com.dev.musicapp.customalertdialog.stylers.AlertItemStyle
import com.dev.musicapp.databinding.ActivitySettingsBinding
import com.dev.musicapp.extensions.getColorByTheme
import com.dev.musicapp.ui.activities.base.BaseActivity

import com.dev.musicapp.utils.BeatConstants
import com.dev.musicapp.utils.SettingsUtility
import org.koin.android.ext.android.inject

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var dialog: AlertDialog
    private val settingsUtility by inject<SettingsUtility>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        init()
    }

    private fun init() {
        dialog = buildThemeDialog()

        binding.let {
            it.executePendingBindings()

            it.lifecycleOwner = this
        }
    }

    private fun buildThemeDialog(): AlertDialog {
        val style = AlertItemStyle()
        style.apply {
            textColor = getColorByTheme(R.attr.titleTextColor)
            selectedTextColor = getColorByTheme(R.attr.colorAccent)
            backgroundColor = getColorByTheme(R.attr.colorPrimarySecondary2)
        }
        return AlertDialog(
            getString(R.string.theme_title),
            getString(R.string.theme_description),
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            addItem(AlertItemAction(
                getString(R.string.default_theme),
                settingsUtility.currentTheme == BeatConstants.AUTO_THEME,
                AlertItemTheme.DEFAULT
            ) {
                it.selected = true
                settingsUtility.currentTheme =
                    BeatConstants.AUTO_THEME
                recreateActivity()
            })
            addItem(AlertItemAction(
                getString(R.string.light_theme),
                settingsUtility.currentTheme == BeatConstants.LIGHT_THEME,
                AlertItemTheme.DEFAULT
            ) {
                it.selected = true
                settingsUtility.currentTheme =
                    BeatConstants.LIGHT_THEME
                recreateActivity()
            })
            addItem(AlertItemAction(
                getString(R.string.dark_theme),
                settingsUtility.currentTheme == BeatConstants.DARK_THEME,
                AlertItemTheme.DEFAULT
            ) {
                it.selected = true
                settingsUtility.currentTheme =
                    BeatConstants.DARK_THEME
                recreateActivity()
            })
        }
    }

    fun showThemes(view: View) {
        try {
            dialog.show(this)
        } catch (ex: IllegalStateException) {
        }
    }
}
