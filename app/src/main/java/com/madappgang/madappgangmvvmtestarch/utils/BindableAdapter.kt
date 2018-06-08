package com.madappgang.madappgangmvvmtestarch.utils

import android.support.v7.widget.RecyclerView

/**
 * Created by Serhii Chaban sc@madappgang.com on 30.05.18.
 */

abstract class BindableAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {


    var clickListener: (T) -> Unit = {}
    var data: MutableList<T> = mutableListOf()

    override fun onBindViewHolder(holder: VH, position: Int) {
        //    val bindable = holder as BindableViewHolder<T>
        // bindable.bindViewHolder(data[position])
    }

    override fun getItemCount(): Int = data.size
    fun addToAnd(newData: List<T>) {
        val positionStart = data.size
        if (newData.isNotEmpty()) {
            data.addAll(newData)
            notifyItemRangeInserted(positionStart, newData.count())
        }
    }

    fun set(newData: List<T>) {
        data.clear()
        if (newData.isNotEmpty()) {
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

}

abstract class ItemCLickListener<T> {
    abstract fun onClick(t: T)
}