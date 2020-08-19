package com.dev.musicapp.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.musicapp.R
import com.dev.musicapp.databinding.FolderItemBinding
import com.dev.musicapp.extensions.inflateWithBinding
import com.dev.musicapp.interfaces.ItemClickListener
import com.dev.musicapp.models.Folder

class FolderAdapter(private val context: Context?) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    var folderList: MutableList<Folder> = mutableListOf()
    var itemClickListener: ItemClickListener<Folder>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = parent.inflateWithBinding<FolderItemBinding>(R.layout.folder_item)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    fun updateDataSet(newList: List<Folder>) {
        folderList = newList.toMutableList()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int) = folderList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: FolderItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(folder: Folder) {
            binding.apply {
                this.folder = folder
                itemMenu.setOnClickListener(this@ViewHolder)
                container.setOnClickListener(this@ViewHolder)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.item_menu -> itemClickListener!!.onPopupMenuClick(
                        view,
                        adapterPosition,
                        getItem(adapterPosition),
                        folderList
                    )
                    R.id.container -> itemClickListener!!.onItemClick(
                        view,
                        adapterPosition,
                        getItem(adapterPosition)
                    )
                }
        }
    }
}