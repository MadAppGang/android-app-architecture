package com.madappgang.architecture.recorder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.madappgang.architecture.recorder.helpers.Recorder.Companion.recordFormat
import kotlinx.android.synthetic.main.cell_item.view.*
import java.io.File


class FolderAdapter(var currentPath: String) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    private lateinit var clickListener: ItemClickListener
    private var dataSet: MutableList<File> = mutableListOf()
    private var pathLength: MutableList<Int> = mutableListOf()
    private var isNormalMode: Boolean = true

    init {
        updateListFiles()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = dataSet[position]
        holder.itemName.text = file.name.removeSuffix(recordFormat)
        holder.itemTypeImage.setImageResource(if (file.isDirectory) {
            R.drawable.ic_folder_open_white_24dp
        } else {
            R.drawable.ic_audiotrack_black_36dp
        })
        if (isNormalMode) {
            holder.itemView.setOnClickListener({
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(dataSet[adapterPosition])
                }
            })
            holder.itemFolderIndicator.visibility = if (file.isDirectory) {
                View.VISIBLE
            } else {
                View.GONE
            }
            holder.itemRemove.visibility = View.GONE
        } else {
            holder.itemFolderIndicator.visibility = View.GONE
            holder.itemRemove.visibility = View.VISIBLE
            holder.itemRemove.setOnClickListener { removeFile(file) }
        }
    }

    fun setPathForAdapter(path: String) {
        pathLength.add(currentPath.length)
        currentPath = path
        updateListFiles()
    }

    fun setLastPathForAdapter() {
        currentPath = currentPath.substring(0, pathLength.last())
        pathLength.remove(pathLength.size - 1)
        updateListFiles()
    }

    fun updateListFiles() {
        dataSet.clear()
        val parentDir = File(currentPath)
        parentDir.listFiles().forEach { if (it.name != "cache") dataSet.add(it) }
        notifyDataSetChanged()
    }

    fun setNormalMode() {
        isNormalMode = true
        notifyDataSetChanged()
    }

    fun setRemoveMode() {
        isNormalMode = false
        notifyDataSetChanged()
    }

    private fun removeFile(file: File) {
        if (file.isDirectory) file.list().forEach { File(file, it).delete() }
        file.delete()
        updateListFiles()
    }

    override fun getItemCount() = dataSet.size

    fun setupItemClickListener(listener: ItemClickListener) {
        clickListener = listener
    }

    interface ItemClickListener {
        fun onItemClick(file: File)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTypeImage: ImageView = view.itemTypeImage
        val itemName: TextView = view.itemName
        val itemFolderIndicator: ImageView = view.itemFolderIndicator
        val itemRemove: ImageView = view.itemRemove
    }
}