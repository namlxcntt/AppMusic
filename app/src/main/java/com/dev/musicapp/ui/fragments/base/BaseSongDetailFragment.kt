package com.quanlv.musicplayer.ui.fragments.base

import android.os.Bundle
import com.dev.musicapp.R
import com.dev.musicapp.extensions.safeActivity
import com.dev.musicapp.models.MediaItem
import com.dev.musicapp.ui.fragments.base.BaseFragment
import com.dev.musicapp.ui.fragments.LyricFragment
import com.dev.musicapp.ui.fragments.SongDetailFragment

open class BaseSongDetailFragment : BaseFragment<MediaItem>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showHideBottomSheet()
    }

    override fun onPause() {
        showHideBottomSheet()
        super.onPause()
    }

    override fun onResume() {
        showHideBottomSheet()
        super.onResume()
    }

    private fun showHideBottomSheet() {
        val currentData = songDetailViewModel.currentData.value ?: return
        if (currentData.id == 0L) return
        val fragment = safeActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (fragment is SongDetailFragment || fragment is LyricFragment) {
            mainViewModel.hideMiniPlayer()
        } else {
            mainViewModel.showMiniPlayer()
        }
    }
}