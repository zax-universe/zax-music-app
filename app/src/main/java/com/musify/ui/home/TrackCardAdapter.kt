package com.musify.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.musify.R
import com.musify.data.model.Track
import com.musify.databinding.ItemCardFeatureBinding

class TrackCardAdapter(private val onClick: (Track) -> Unit) :
    ListAdapter<Track, TrackCardAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(a: Track, b: Track) = a.id == b.id
            override fun areContentsTheSame(a: Track, b: Track) = a == b
        }
    }

    inner class ViewHolder(val binding: ItemCardFeatureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.tvTitle.text = track.title
            binding.tvArtist.text = track.artist
            Glide.with(binding.root).load(track.albumArtUrl)
                .placeholder(R.drawable.ic_musify_logo).into(binding.ivArt)
            binding.root.setOnClickListener { onClick(track) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemCardFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
