package com.musify.ui.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.musify.databinding.FragmentDownloadsBinding
import com.musify.ui.home.TrackAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadsFragment : Fragment() {

    private var _binding: FragmentDownloadsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DownloadsViewModel by viewModels()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setupRecyclerView()
        observeDownloads()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter { track ->
            val intent = android.content.Intent(requireContext(), com.musify.ui.player.PlayerActivity::class.java).apply {
                putExtra("TRACK_ID", track.id)
                putExtra("TRACK_TITLE", track.title)
                putExtra("TRACK_ARTIST", track.artist)
                putExtra("TRACK_URL", track.previewUrl)
                putExtra("TRACK_ART", track.albumArtUrl)
            }
            startActivity(intent)
        }
        binding.rvDownloads.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDownloads.adapter = adapter
    }

    private fun observeDownloads() {
        viewModel.downloads.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks)
            val empty = tracks.isEmpty()
            binding.layoutEmpty.visibility = if (empty) View.VISIBLE else View.GONE
            binding.rvDownloads.visibility = if (empty) View.GONE else View.VISIBLE
        }
        viewModel.storageUsed.observe(viewLifecycleOwner) { size ->
            binding.tvStorageInfo.text = "Storage used: $size"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
