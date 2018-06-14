/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.madappgang.recordings.R
import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Track
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

    fun setData(items: List<Foldable>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun removeAt(index: Int) {
        data.remove(data.get(index))
        notifyItemRemoved(index)
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var onFolderItemClicked: (Folder) -> Unit = { folder -> }
        var onTrackItemClicked: (Track) -> Unit = { recording -> }
        var onRemoveItemClicked: (Foldable, Int) -> Unit = { file, index -> }

        val item = itemView.item
        val nameView = itemView.fileName
        val swipeView = itemView.swipeView
        val arrowRight = itemView.arrowRight
        val delete = itemView.delete
        val itemIcon = itemView.itemIcon

        fun init(foldable: Foldable) {
            when (foldable) {
                is Folder -> initAs(foldable)
                is Track -> initAs(foldable)
            }
            delete.setOnClickListener {
                onRemoveItemClicked(foldable, adapterPosition)
            }
        }

        fun initAs(folder: Folder) {
            nameView.text = folder.name
            itemIcon.setImageResource(R.drawable.ic_folder_black_24dp)
            arrowRight.visibility = View.VISIBLE
            item.setOnClickListener { onFolderItemClicked(folder) }
        }

        fun initAs(track: Track) {
            nameView.text = track.name
            itemIcon.setImageResource(R.drawable.ic_music_note_black_24dp)
            arrowRight.visibility = View.GONE
            item.setOnClickListener { onTrackItemClicked(track) }
        }
    }
}