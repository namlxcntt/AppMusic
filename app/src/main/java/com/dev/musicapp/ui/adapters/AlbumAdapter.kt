package com.dev.musicapp.ui.adapters

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dev.musicapp.R
import com.dev.musicapp.customalertdialog.utils.ViewUtils.dip2px
import com.dev.musicapp.databinding.AlbumItemBinding
import com.dev.musicapp.databinding.AlbumItemHeaderBinding
import com.dev.musicapp.databinding.ArtistDetailItemBinding
import com.dev.musicapp.extensions.inflateWithBinding

import com.dev.musicapp.interfaces.ItemClickListener
import com.dev.musicapp.models.Album
import com.dev.musicapp.utils.GeneralUtils.screenWidth

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

class AlbumAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastClick = 0L

    var albumList: MutableList<Album> = mutableListOf()
    var showHeader = false
    var artistDetail = false
    var itemClickListener: ItemClickListener<Album>? = null
    var spanCount: Int = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemId = if (artistDetail) R.layout.artist_detail_item else R.layout.album_item
        return when (viewType) {
            HEADER_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<AlbumItemHeaderBinding>(R.layout.album_item_header)
                ViewHolderAlbumHeader(viewBinding)
            }
            ITEM_TYPE -> {
                val viewBinding = parent.inflateWithBinding<ViewDataBinding>(itemId)
                ViewHolderAlbum(viewBinding)
            }
            else -> {
                val viewBinding = parent.inflateWithBinding<ViewDataBinding>(itemId)
                ViewHolderAlbum(viewBinding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showHeader && position == 0) {
            HEADER_TYPE
        } else {
            ITEM_TYPE
        }
    }

    override fun getItemCount() = if (showHeader) {
        albumList.size + 1
    } else {
        albumList.size
    }

    fun getItem(position: Int): Album? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                albumList[position - 1]
            }
        } else {
            albumList[position]
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentSong = if (!albumList.isNullOrEmpty()) getItem(position) else null
        when (getItemViewType(position)) {
            HEADER_TYPE -> {
                (holder as ViewHolderAlbumHeader).bind(albumList.size)
            }
            ITEM_TYPE -> {
                (holder as ViewHolderAlbum).bind(currentSong!!)
            }
        }
    }

    fun updateDataSet(newList: List<Album>) {
        albumList = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolderAlbum(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(album: Album) {
            if (binding is AlbumItemBinding)
                binding.apply {
                    this.album = album
                    binding.executePendingBindings()

                    showDetails.setOnClickListener(this@ViewHolderAlbum)
                    container.layoutParams.apply {
                        height = screenWidth / spanCount + dip2px(context!!, 42)
                        width = screenWidth / spanCount - dip2px(context, 6)
                    }
                }
            else {
                (binding as ArtistDetailItemBinding).apply {
                    this.album = album
                    binding.executePendingBindings()

                    binding.root.setOnClickListener(this@ViewHolderAlbum)
                }
            }
        }

        override fun onClick(view: View) {
            if (SystemClock.elapsedRealtime() - lastClick < 500) {
                return
            }
            lastClick = SystemClock.elapsedRealtime()
            if (itemClickListener != null) itemClickListener!!.onItemClick(
                view,
                adapterPosition,
                getItem(adapterPosition)!!
            )
        }
    }

    inner class ViewHolderAlbumHeader(private val binding: AlbumItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(albumCount: Int) {
            binding.apply {
                this.albumCount = albumCount
                executePendingBindings()

                sortAlbum.setOnClickListener(this@ViewHolderAlbumHeader)
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                itemClickListener!!.onSortClick(view)
        }
    }
}