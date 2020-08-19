package com.dev.musicapp.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dev.musicapp.R
import com.dev.musicapp.databinding.FragmentPlaylistDetailBinding
import com.dev.musicapp.extensions.*
import com.dev.musicapp.models.Song
import com.dev.musicapp.ui.fragments.base.BaseFragment
import com.dev.musicapp.ui.viewmodels.PlaylistViewModel
import com.dev.musicapp.ui.adapters.SongAdapter

import com.dev.musicapp.utils.BeatConstants.PLAY_ALL_SHUFFLED
import com.dev.musicapp.utils.BeatConstants.PLAY_LIST_DETAIL
import com.dev.musicapp.utils.GeneralUtils.getExtraBundle
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject


class PlaylistDetailFragment : BaseFragment<Song>() {

    lateinit var binding: FragmentPlaylistDetailBinding
    private lateinit var songAdapter: SongAdapter

    private val playlistViewModel by inject<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_playlist_detail, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val id = arguments!!.getLong(PLAY_LIST_DETAIL)

        binding.playlist = playlistViewModel.getPlaylist(id)

        songAdapter = SongAdapter().apply {
            showHeader = true
            isAlbumDetail = true
            itemClickListener = this@PlaylistDetailFragment
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            clipToOutline = true
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        playlistViewModel.getSongs(binding.playlist!!.id)
            .filter { !songAdapter.songList.deepEquals(it) }
            .observe(this) {
                songAdapter.updateDataSet(it)
                mainViewModel.reloadQueueIds(it.toIDList(), binding.playlist!!.name)
            }

        binding.let {
            it.viewModel = playlistViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun removeFromList(playListId: Long, item: Song?) {
        playlistViewModel.remove(playListId, item!!.id)
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.playlist!!.name)
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.playlist!!.name)
        mainViewModel.transportControls()?.sendCustomAction(PLAY_ALL_SHUFFLED, extras)
    }

    override fun onPlayAllClick(view: View) {
        if (songAdapter.songList.isEmpty()) return
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.playlist!!.name)
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
