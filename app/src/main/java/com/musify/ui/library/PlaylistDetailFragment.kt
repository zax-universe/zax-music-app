package com.musify.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.musify.databinding.FragmentPlaylistDetailBinding
import com.musify.ui.home.TrackAdapter
import com.musify.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistDetailViewModel by viewModels()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getLong("playlistId") ?: return
        viewModel.loadPlaylist(playlistId)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = TrackAdapter { track ->
            startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("TRACK_ID", track.id); putExtra("TRACK_TITLE", track.title)
                putExtra("TRACK_ARTIST", track.artist); putExtra("TRACK_URL", track.previewUrl)
                putExtra("TRACK_ART", track.albumArtUrl)
            })
        }
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter

        viewModel.tracks.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.playlist.observe(viewLifecycleOwner) { p ->
            p?.let { binding.toolbar.title = it.name }
        }

        binding.fabPlay.setOnClickListener {
            viewModel.tracks.value?.firstOrNull()?.let { track ->
                startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra("TRACK_ID", track.id); putExtra("TRACK_TITLE", track.title)
                    putExtra("TRACK_ARTIST", track.artist); putExtra("TRACK_URL", track.previewUrl)
                    putExtra("TRACK_ART", track.albumArtUrl)
                })
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
