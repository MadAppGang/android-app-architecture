/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/16/18.
 */

package com.madappgang.recordings.pages.folder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.madappgang.recordings.R
import com.madappgang.recordings.models.Foldable
import com.madappgang.recordings.models.Folder
import com.madappgang.recordings.models.Track
import kotlinx.android.synthetic.main.item_foldable_adapter.view.*

internal class FoldableAdapter : RecyclerView.Adapter<FoldableAdapter.FileViewHolder>() {

    var onFolderItemClicked: (Folder) -> Unit = { folder -> }
    var onTrackItemClicked: (Track) -> Unit = { recording -> }
    var onRemoveItemClicked: (Foldable, Int) -> Unit = { file, index -> }

    private val data = ArrayList<Foldable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_foldable_adapter, parent, false)

        return FileViewHolder(view).apply {
            onFolderItemClicked = this@FoldableAdapter.onFolderItemClicked
            onTrackItemClicked = this@FoldableAdapter.onTrackItemClicked
            onRemoveItemClicked = this@FoldableAdapter.onRemoveItemClicked
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.init(data[position])
    }

    fun set(items: List<Foldable>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun remove(index: Int) {
        data.remove(data[index])
        notifyItemRemoved(index)
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var onFolderItemClicked: (Folder) -> Unit = { folder -> }
        var onTrackItemClicked: (Track) -> Unit = { recording -> }
        var onRemoveItemClicked: (Foldable, Int) -> Unit = { file, index -> }

        fun init(foldable: Foldable) {
            when (foldable) {
                is Folder -> init(foldable)
                is Track -> init(foldable)
            }
            itemView.delete.setOnClickListener {
                onRemoveItemClicked(foldable, adapterPosition)
            }
        }

        private fun init(folder: Folder) {
            itemView.fileName.text = folder.name
            itemView.itemIcon.setImageResource(R.drawable.ic_folder_black_24dp)
            itemView.arrowRight.visibility = View.VISIBLE
            itemView.item.setOnClickListener { onFolderItemClicked(folder) }
        }

        private fun init(track: Track) {
            itemView.fileName.text = track.name
            itemView.itemIcon.setImageResource(R.drawable.ic_music_note_black_24dp)
            itemView.arrowRight.visibility = View.GONE
            itemView.item.setOnClickListener { onTrackItemClicked(track) }
        }
    }
}