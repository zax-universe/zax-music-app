package com.musify.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.musify.R
import com.musify.data.model.Track
import com.musify.databinding.ItemTrackBinding

class TrackAdapter(private val onClick: (Track) -> Unit) :
    ListAdapter<Track, TrackAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(a: Track, b: Track) = a.id == b.id
            override fun areContentsTheSame(a: Track, b: Track) = a == b
        }
    }

    inner class ViewHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.tvTitle.text = track.title
            binding.tvArtist.text = track.artist
            binding.ivDownloaded.visibility = if (track.isDownloaded) android.view.View.VISIBLE else android.view.View.GONE
            Glide.with(binding.root).load(track.albumArtUrl)
                .placeholder(R.drawable.ic_musify_logo).error(R.drawable.ic_musify_logo)
                .into(binding.ivAlbumArt)
            binding.root.setOnClickListener { onClick(track) }
            binding.btnMore.setOnClickListener { /* show bottom sheet */ }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
