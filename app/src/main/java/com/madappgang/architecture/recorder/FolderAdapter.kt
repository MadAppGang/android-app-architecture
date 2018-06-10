package com.madappgang.architecture.recorder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_item.view.*

class FolderAdapter(private val myDataset: Array<String>) :
        RecyclerView.Adapter<FolderAdapter.ViewHolder>() {
    lateinit var clickListener: ItemClickListener

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val item_type_image = view.item_type_image
        val item_name = view.item_name
        val item_folder_indicator = view.item_folder_indicator
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FolderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_item, parent, false)
        val h = ViewHolder(view)
        view.setOnClickListener({ it ->
            val adapterPosition = h.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(myDataset[adapterPosition])
            }
        })

        return h
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item_name.text = myDataset[position]
        holder.item_folder_indicator.visibility = View.VISIBLE
        holder.item_type_image.setImageResource(R.drawable.ic_audiotrack_purple_a100_36dp)
    }

    override fun getItemCount() = myDataset.size


    fun setupItemClickListener(listener: ItemClickListener) {
        clickListener = listener
    }

    interface ItemClickListener {
        fun onItemClick(title: String)
    }
}