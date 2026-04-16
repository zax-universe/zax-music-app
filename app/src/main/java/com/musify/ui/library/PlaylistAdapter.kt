package com.musify.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.musify.R
import com.musify.data.local.entity.PlaylistEntity
import com.musify.databinding.ItemPlaylistBinding

class PlaylistAdapter(
    private val onClick: (PlaylistEntity) -> Unit,
    private val onMore: (PlaylistEntity) -> Unit
) : ListAdapter<PlaylistEntity, PlaylistAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PlaylistEntity>() {
            override fun areItemsTheSame(a: PlaylistEntity, b: PlaylistEntity) = a.id == b.id
            override fun areContentsTheSame(a: PlaylistEntity, b: PlaylistEntity) = a == b
        }
    }

    inner class ViewHolder(val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: PlaylistEntity) {
            binding.tvName.text = playlist.name
            val count = if (playlist.trackIds.isEmpty()) 0 else playlist.trackIds.split(",").size
            binding.tvCount.text = "$count songs"
            
            Glide.with(binding.root)
                .load(playlist.coverArtUrl)
                .placeholder(R.drawable.ic_library_music)
                .into(binding.ivCover)
            
            binding.root.setOnClickListener { onClick(playlist) }
            binding.btnMore.setOnClickListener { onMore(playlist) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
