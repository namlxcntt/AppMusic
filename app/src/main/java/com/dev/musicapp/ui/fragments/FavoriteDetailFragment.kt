package com.dev.musicapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dev.musicapp.R
import com.dev.musicapp.databinding.FragmentFavoriteDetailBinding
import com.dev.musicapp.extensions.*

import com.dev.musicapp.models.Song
import com.dev.musicapp.ui.fragments.base.BaseFragment
import com.dev.musicapp.ui.viewmodels.FavoriteViewModel
import com.dev.musicapp.ui.viewmodels.PlaylistViewModel
import com.dev.musicapp.utils.BeatConstants.FAVORITE_KEY
import com.dev.musicapp.utils.BeatConstants.PLAY_ALL_SHUFFLED
import com.dev.musicapp.ui.adapters.SongAdapter
import com.dev.musicapp.utils.GeneralUtils.getExtraBundle
import org.koin.android.ext.android.inject

class FavoriteDetailFragment : BaseFragment<Song>() {

    private lateinit var binding: FragmentFavoriteDetailBinding
    private lateinit var songAdapter: SongAdapter
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()

    private val viewModel by inject<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_favorite_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val id = arguments!!.getLong(FAVORITE_KEY)
        binding.favorite = favoriteViewModel.getFavorite(id)

        songAdapter = SongAdapter().apply {
            showHeader = true
            isAlbumDetail = true
            itemClickListener = this@FavoriteDetailFragment
        }

        viewModel.songListFavorite(id).observe(this) {
            if (it.isEmpty()) {
                favoriteViewModel.deleteFavorites(longArrayOf(id))
                safeActivity.onBackPressed()
            } else if (!songAdapter.songList.deepEquals(it)) {
                songAdapter.updateDataSet(it)
                mainViewModel.reloadQueueIds(it.toIDList(), getString(R.string.favorite_music))
            }
        }

        binding.apply {
            list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras =
            getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.favorite_music))
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras =
            getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.favorite_music))
        mainViewModel.transportControls()?.sendCustomAction(PLAY_ALL_SHUFFLED, extras)
    }

    override fun onPlayAllClick(view: View) {
        val extras =
            getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.favorite_music))
        mainViewModel.mediaItemClicked(songAdapter.songList.first().toMediaItem(), extras)
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
