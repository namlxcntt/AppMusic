package com.dev.musicapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.dev.musicapp.R
import com.dev.musicapp.databinding.FragmentLibraryBinding

import com.dev.musicapp.extensions.inflateWithBinding
import com.dev.musicapp.extensions.safeActivity
import com.dev.musicapp.ui.activities.MainActivity
import com.dev.musicapp.ui.adapters.ViewPagerAdapter
import com.quanlv.musicplayer.ui.fragments.base.BaseSongDetailFragment
import com.dev.musicapp.utils.AutoClearBinding
import com.quanlv.musicplayer.ui.fragments.*


class LibraryFragment : BaseSongDetailFragment() {

    private var binding by AutoClearBinding<FragmentLibraryBinding>(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_library, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val didPermissionsGrant = (safeActivity as MainActivity).didPermissionsGrant()
        if (didPermissionsGrant) init()
        binding.apply {
            viewModel = mainViewModel
            this.didPermissionsGrant = didPermissionsGrant
            executePendingBindings()

            lifecycleOwner = this@LibraryFragment
        }
    }

    private fun init() {
        binding.apply {
            initViewPager(binding.pagerSortMode)
            tabsContainer.setupWithViewPager(pagerSortMode)
        }
    }

    private fun initViewPager(viewPager: ViewPager) {
        val listSortModeAdapter = ViewPagerAdapter(safeActivity.supportFragmentManager).apply {
            addFragment(FavoriteFragment(), getString(R.string.favorites))
            addFragment(PlaylistFragment(), getString(R.string.playlists))
            addFragment(SongFragment(), getString(R.string.songs))
            addFragment(AlbumFragment(), getString(R.string.albums))
            addFragment(ArtistFragment(), getString(R.string.artists))
//            addFragment(FolderFragment(), getString(R.string.folders))
        }

        viewPager.apply {
            adapter = listSortModeAdapter
            offscreenPageLimit = 1
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(s: Int) = Unit
                override fun onPageScrolled(p: Int, po: Float, pop: Int) = Unit
                override fun onPageSelected(p: Int) {
                    settingsUtility.startPageIndexSelected = p
                }
            })
            setCurrentItem(settingsUtility.startPageIndexSelected, false)
        }
    }
}
