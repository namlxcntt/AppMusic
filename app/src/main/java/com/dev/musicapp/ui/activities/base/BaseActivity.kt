package com.dev.musicapp.ui.activities.base

import android.content.Intent
import android.graphics.Typeface
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.dev.musicapp.R
import com.dev.musicapp.extensions.DEFAULT
import com.dev.musicapp.extensions.snackbar
import com.dev.musicapp.ui.activities.SettingsActivity
import com.dev.musicapp.utils.SettingsUtility
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.dev.musicapp.extensions.addFragment
import com.dev.musicapp.extensions.getColorByTheme
import com.dev.musicapp.ui.fragments.SearchFragment
import com.dev.musicapp.utils.BeatConstants

open class BaseActivity : RequestPermissionActivity() {

    private var currentTheme: String? = null
    private var powerMenu: PowerMenu? = null

    private val settingsUtility by inject<SettingsUtility>()

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, _ ->
        when (position) {
            0 -> {
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)

                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, 2)
                } else {
                    main_container.snackbar(
                        DEFAULT,
                        getString(R.string.no_eq),
                        LENGTH_SHORT
                    )
                }
            }
            1 -> {
                val intent = Intent(this@BaseActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        powerMenu!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onBackPressed() {
        if (powerMenu != null) {
            if (powerMenu!!.isShowing) {
                powerMenu!!.dismiss()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (settingsUtility.currentTheme != currentTheme) {
            recreateActivity()
        }
    }

    private fun init() {
        currentTheme = settingsUtility.currentTheme
        setAppTheme(currentTheme!!)

        powerMenu = initPopUpMenu().setOnMenuItemClickListener(onMenuItemClickListener).build()
    }

    override fun recreateActivity() {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        startActivity(intent.apply {
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }


    fun back(view: View) {
        onBackPressed()
    }

    fun menu(view: View) {
        powerMenu?.showAsAnchorRightTop(view)
    }

    fun search(view: View) {
        addFragment(
            R.id.nav_host_fragment,
            SearchFragment(),
            BeatConstants.SONG_DETAIL,
            true
        )
    }

    private fun initPopUpMenu(): PowerMenu.Builder {
        // Build Popup Menu

        return PowerMenu.Builder(this)
            .addItem(PowerMenuItem(getString(R.string.equalizer), false))
            .addItem(PowerMenuItem(getString(R.string.settings), false))
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setMenuRadius(this.resources.getDimension(R.dimen.popupMenuRadius))
            .setMenuShadow(10f)
            .setShowBackground(false)
            .setTextColor(getColorByTheme(R.attr.titleTextColor))
            .setTextGravity(Gravity.START)
            .setTextSize(16)
            .setTextTypeface(Typeface.createFromAsset(assets, "fonts/product_sans_regular.ttf"))
            .setSelectedTextColor(getColorByTheme(R.attr.colorAccent))
            .setMenuColor(getColorByTheme(R.attr.colorPrimarySecondary2))
            .setSelectedMenuColor(getColorByTheme(R.attr.colorPrimarySecondary))
    }

    private fun setAppTheme(current_theme: String) {
        when (current_theme) {
            BeatConstants.DARK_THEME -> setTheme(R.style.AppTheme_Dark)
            BeatConstants.LIGHT_THEME -> setTheme(R.style.AppTheme_Light)
            else -> setTheme(R.style.AppTheme_Auto)
        }
    }
}
