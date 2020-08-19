package com.dev.musicapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dev.musicapp.R
import com.dev.musicapp.alertdialog.actions.AlertItemAction
import com.dev.musicapp.alertdialog.enums.AlertItemTheme
import com.dev.musicapp.databinding.FragmentSongBinding
import com.dev.musicapp.extensions.*

import com.dev.musicapp.models.Song
import com.dev.musicapp.ui.fragments.base.BaseFragment
import com.dev.musicapp.ui.viewmodels.PlaylistViewModel
import com.dev.musicapp.ui.viewmodels.SongViewModel
import com.dev.musicapp.ui.adapters.SongAdapter
import com.dev.musicapp.utils.AutoClearBinding
import com.dev.musicapp.utils.BeatConstants.PLAY_ALL_SHUFFLED
import com.dev.musicapp.utils.GeneralUtils.getExtraBundle
import com.dev.musicapp.utils.SortModes
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject

class SongFragment : BaseFragment<Song>() {

    private lateinit var songAdapter: SongAdapter
    private var binding by AutoClearBinding<FragmentSongBinding>(this)

    private val playlistViewModel by inject<PlaylistViewModel>()
    private val viewModel by inject<SongViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_song, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        songAdapter = SongAdapter().apply {
            showHeader = true
            itemClickListener = this@SongFragment
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        viewModel.getSongList()
            .filter { !songAdapter.songList.deepEquals(it) }
            .observe(this) {
                songAdapter.updateDataSet(it)
                if (songAdapter.songList.isNotEmpty())
                    mainViewModel.reloadQueueIds(it.toIDList(), getString(R.string.all_songs))
            }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }

        createDialog()
    }

    private fun createDialog() {
        dialog = buildDialog(
            getString(R.string.sort_title),
            getString(R.string.sort_msg),
            listOf(
                AlertItemAction(
                    context!!.getString(R.string.sort_az),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_A_Z,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_A_Z
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_za),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_Z_A,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_Z_A
                },
                AlertItemAction(
                    context!!.getString(R.string.album),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_ALBUM,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_ALBUM
                },
                AlertItemAction(
                    context!!.getString(R.string.artist),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_ARTIST,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_ARTIST
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_duration),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_DURATION,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_DURATION
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_year),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_YEAR,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_YEAR
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_last_added),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_LAST_ADDED,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_LAST_ADDED
                }
            ))
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.all_songs))
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.all_songs))
        mainViewModel.transportControls()?.sendCustomAction(PLAY_ALL_SHUFFLED, extras)
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }

    override fun onPlayAllClick(view: View) {
        if (songAdapter.songList.isEmpty()) return
        val extras = getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.all_songs))
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