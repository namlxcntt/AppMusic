package com.dev.musicapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.dev.musicapp.R
import com.dev.musicapp.alertdialog.actions.AlertItemAction
import com.dev.musicapp.alertdialog.enums.AlertItemTheme
import com.dev.musicapp.databinding.FragmentAlbumBinding
import com.dev.musicapp.extensions.*

import com.dev.musicapp.models.Album
import com.dev.musicapp.ui.fragments.base.BaseFragment
import com.dev.musicapp.ui.adapters.AlbumAdapter
import com.dev.musicapp.ui.viewmodels.AlbumViewModel
import com.dev.musicapp.utils.BeatConstants
import com.dev.musicapp.utils.BeatConstants.ALBUM_KEY
import com.dev.musicapp.utils.GeneralUtils
import com.dev.musicapp.utils.GeneralUtils.PORTRAIT
import com.dev.musicapp.utils.SortModes
import org.koin.android.ext.android.inject

class AlbumFragment : BaseFragment<Album>() {

    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var binding: FragmentAlbumBinding
    private val albumViewModel by inject<AlbumViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_album, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val sc = if (GeneralUtils.getOrientation(safeActivity) == PORTRAIT) 2 else 5

        albumAdapter = AlbumAdapter(context).apply {
            showHeader = true
            itemClickListener = this@AlbumFragment
            spanCount = sc
        }

        binding.list.apply {
            layoutManager = GridLayoutManager(context, sc).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) sc else 1
                    }
                }
            }
            adapter = albumAdapter
        }

        albumViewModel.getAlbums()
            .filter { !albumAdapter.albumList.deepEquals(it) }
            .observe(this) { list ->
                albumAdapter.updateDataSet(list)
            }

        binding.let {
            it.viewModel = albumViewModel
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
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_A_Z,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_A_Z
                    reloadAdapter()
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_za),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_Z_A,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_Z_A
                    reloadAdapter()
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_year),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_YEAR,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder =
                        SortModes.AlbumModes.ALBUM_YEAR
                    reloadAdapter()
                },
                AlertItemAction(
                    context!!.getString(R.string.artist),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_ARTIST,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_ARTIST
                    reloadAdapter()
                },
                AlertItemAction(
                    context!!.getString(R.string.song_count),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_SONG_COUNT,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_SONG_COUNT
                    reloadAdapter()
                }
            )
        )
    }

    private fun reloadAdapter() {
        albumViewModel.update()
    }

    override fun onItemClick(view: View, position: Int, item: Album) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            AlbumDetailFragment(),
            BeatConstants.ALBUM_DETAIL,
            true,
            bundleOf(ALBUM_KEY to item.id)
        )
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Album, itemList: List<Album>) {
        Toast.makeText(context, "Menu of " + item.title, Toast.LENGTH_SHORT).show()
    }

    override fun onPlayAllClick(view: View) {
        Toast.makeText(context, "Shuffle", Toast.LENGTH_SHORT).show()
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }
}
