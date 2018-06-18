package com.madappgang.madappgangmvvmtestarch.ui.recordings

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.madappgang.madappgangmvvmtestarch.databinding.ListItemRecordingBinding
import android.view.LayoutInflater
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.utils.BindableAdapter


/**
 * Created by Serhii Chaban sc@madappgang.com on 30.05.18.
 */
class RecordingsAdapter : BindableAdapter<SourceFile, RecordingsViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecordingsViewHolder(ListItemRecordingBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecordingsViewHolder, position: Int) {
        holder.bind(data[position])
        holder.binding.clickListener = View.OnClickListener { clickListener.invoke(data[position]) }
    }
}

class RecordingsViewHolder(val binding: ListItemRecordingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: SourceFile) {
        binding.sourceFile = item
        binding.executePendingBindings()
    }

}