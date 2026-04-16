package com.musify.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.musify.databinding.FragmentHomeBinding
import com.musify.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recentAdapter: TrackAdapter
    private lateinit var trendingAdapter: TrackAdapter
    private lateinit var featuredAdapter: TrackCardAdapter
    private lateinit var newReleasesAdapter: TrackCardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGreeting.text = getGreeting()
        setupAdapters()
        observeViewModel()
    }

    private fun getGreeting(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun setupAdapters() {
        val onClick = { track: com.musify.data.model.Track ->
            Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("TRACK_ID", track.id)
                putExtra("TRACK_TITLE", track.title)
                putExtra("TRACK_ARTIST", track.artist)
                putExtra("TRACK_URL", track.previewUrl)
                putExtra("TRACK_ART", track.albumArtUrl)
            }.also { startActivity(it) }
        }

        recentAdapter = TrackAdapter(onClick)
        binding.rvRecentlyPlayed.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = recentAdapter
        }

        trendingAdapter = TrackAdapter(onClick)
        binding.rvTrending.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trendingAdapter
        }

        featuredAdapter = TrackCardAdapter(onClick)
        binding.rvFeatured.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = featuredAdapter
        }

        newReleasesAdapter = TrackCardAdapter(onClick)
        binding.rvNewReleases.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleasesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.recentTracks.observe(viewLifecycleOwner) { recentAdapter.submitList(it) }
        viewModel.trendingTracks.observe(viewLifecycleOwner) { trendingAdapter.submitList(it.take(6)) }
        viewModel.featuredTracks.observe(viewLifecycleOwner) { featuredAdapter.submitList(it) }
        viewModel.newReleases.observe(viewLifecycleOwner) { newReleasesAdapter.submitList(it) }
        viewModel.error.observe(viewLifecycleOwner) {
            binding.layoutError.visibility = if (it != null) View.VISIBLE else View.GONE
        }
        binding.btnRetry.setOnClickListener { viewModel.loadAll() }
    }

    override fun onDestroyView() { 
        super.onDestroyView()
        _binding = null 
    }
}
