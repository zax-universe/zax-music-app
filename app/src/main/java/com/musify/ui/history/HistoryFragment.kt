package com.musify.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.musify.databinding.FragmentHistoryBinding
import com.musify.ui.home.TrackAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeHistory()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter { track ->
            // Launch player
            val intent = android.content.Intent(requireContext(), com.musify.ui.player.PlayerActivity::class.java).apply {
                putExtra("TRACK_ID", track.id)
                putExtra("TRACK_TITLE", track.title)
                putExtra("TRACK_ARTIST", track.artist)
                putExtra("TRACK_URL", track.previewUrl)
                putExtra("TRACK_ART", track.albumArtUrl)
            }
            startActivity(intent)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    private fun observeHistory() {
        viewModel.history.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks)
            binding.layoutEmpty.visibility = if (tracks.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHistory.visibility = if (tracks.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
