package com.dev.musicapp.ui.adapters

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.musicapp.R
import com.dev.musicapp.alertdialog.utils.ViewUtils.dip2px
import com.dev.musicapp.databinding.ArtistItemBinding
import com.dev.musicapp.databinding.ArtistItemHeaderBinding
import com.dev.musicapp.extensions.inflateWithBinding

import com.dev.musicapp.interfaces.ItemClickListener
import com.dev.musicapp.models.Artist
import com.dev.musicapp.utils.GeneralUtils.screenWidth

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

class ArtistAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var artistList: List<Artist> = emptyList()
    var showHeader: Boolean = false
    var itemClickListener: ItemClickListener<Artist>? = null
    var spanCount: Int = 0
    private var lastClick = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<ArtistItemHeaderBinding>(R.layout.artist_item_header)
                ViewHolderAlbumHeader(viewBinding)
            }
            ITEM_TYPE -> {
                val viewBinding = parent.inflateWithBinding<ArtistItemBinding>(R.layout.artist_item)
                ViewHolderArtist(viewBinding)
            }
            else -> {
                val viewBinding = parent.inflateWithBinding<ArtistItemBinding>(R.layout.artist_item)
                ViewHolderArtist(viewBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentSong = if (!artistList.isNullOrEmpty()) getItem(position) else null
        when (getItemViewType(position)) {
            HEADER_TYPE -> {
                (holder as ViewHolderAlbumHeader).bind(artistList.size)
            }
            ITEM_TYPE -> {
                (holder as ViewHolderArtist).bind(currentSong!!)
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
        artistList.size + 1
    } else {
        artistList.size
    }

    fun getItem(position: Int): Artist? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                artistList[position - 1]
            }
        } else {
            artistList[position]
        }
    }

    fun updateDataSet(newList: List<Artist>) {
        this.artistList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolderArtist(private val binding: ArtistItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(artist: Artist) {
            binding.apply {
                this.artist = artist
                executePendingBindings()

                showDetails.setOnClickListener(this@ViewHolderArtist)
                container.layoutParams.apply {
                    height = screenWidth / spanCount + dip2px(context!!, 42)
                    width = screenWidth / spanCount - dip2px(context, 6)
                }
            }
        }

        override fun onClick(view: View) {
            if (SystemClock.elapsedRealtime() - lastClick < 300) {
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

    inner class ViewHolderAlbumHeader(private val binding: ArtistItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(artistCount: Int) {
            binding.apply {
                this.artistCount = artistCount
                executePendingBindings()

                sortArtist.setOnClickListener(this@ViewHolderAlbumHeader)
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                itemClickListener!!.onSortClick(view)
        }
    }
}