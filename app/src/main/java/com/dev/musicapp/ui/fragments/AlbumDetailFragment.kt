package com.dev.musicapp.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dev.musicapp.R
import com.dev.musicapp.databinding.FragmentAlbumDetailBinding
import com.dev.musicapp.extensions.*

import com.dev.musicapp.models.Album
import com.dev.musicapp.models.Song
import com.dev.musicapp.ui.fragments.base.BaseFragment
import com.dev.musicapp.ui.viewmodels.FavoriteViewModel
import com.dev.musicapp.ui.viewmodels.PlaylistViewModel
import com.dev.musicapp.ui.adapters.SongAdapter
import com.dev.musicapp.ui.viewmodels.AlbumViewModel

import com.dev.musicapp.utils.BeatConstants
import com.dev.musicapp.utils.GeneralUtils.getExtraBundle
import com.dev.musicapp.utils.GeneralUtils.getTotalTime
import kotlinx.android.synthetic.main.fragment_album.*
import org.koin.android.ext.android.inject

class AlbumDetailFragment : BaseFragment<Song>() {

    private lateinit var album: Album
    private lateinit var songAdapter: SongAdapter
    private lateinit var binding: FragmentAlbumDetailBinding
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val albumViewModel by inject<AlbumViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_album_detail, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        val id = arguments!!.getLong(BeatConstants.ALBUM_KEY)
        album = albumViewModel.getAlbum(id)
        initNeeded(Song(), emptyList(), id)
        songAdapter = SongAdapter().apply {
            itemClickListener = this@AlbumDetailFragment
            showHeader = true
            isAlbumDetail = true
            showCover = false
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            clipToOutline = true
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        binding.addFavorites.setOnClickListener { toggleAddFav() }

        albumViewModel.getSongsByAlbum(album.id)
            .filter { !songAdapter.songList.deepEquals(it) }
            .observe(this) {
                songAdapter.updateDataSet(it)
                mainViewModel.reloadQueueIds(it.toIDList(), album.title)
                binding.totalDuration = getTotalTime(songAdapter.songList).toInt()
                if (it.isEmpty()) {
                    favoriteViewModel.deleteFavorite(id)
                    safeActivity.onBackPressed()
                }
            }

        binding.let {
            it.viewModel = albumViewModel
            it.mainViewModel = mainViewModel
            it.album = album
            it.executePendingBindings()

            it.lifecycleOwner = this
        }
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), album.title)
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), album.title)
        mainViewModel.transportControls()?.sendCustomAction(BeatConstants.PLAY_ALL_SHUFFLED, extras)
    }

    override fun onPlayAllClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), BeatConstants.ALBUM_KEY)
        mainViewModel.mediaItemClicked(songAdapter.songList.first().toMediaItem(), extras)
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }

    private fun toggleAddFav() {
        if (favoriteViewModel.favExist(album.id)) {
            val resp = favoriteViewModel.deleteFavorites(longArrayOf(album.id))
            showSnackBar(view, resp, R.string.album_no_fav_ok)
        } else {
            val resp = favoriteViewModel.create(album.toFavorite())
            showSnackBar(view, resp, R.string.album_fav_ok)
        }
    }
}
