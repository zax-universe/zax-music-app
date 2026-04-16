package com.musify.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.musify.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(
    private val onQueryClick: (String) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : ListAdapter<String, SearchHistoryAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(a: String, b: String) = a == b
            override fun areContentsTheSame(a: String, b: String) = a == b
        }
    }

    inner class ViewHolder(val binding: ItemSearchHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(query: String) {
            binding.tvQuery.text = query
            binding.root.setOnClickListener { onQueryClick(query) }
            binding.btnRemove.setOnClickListener { onRemoveClick(query) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
