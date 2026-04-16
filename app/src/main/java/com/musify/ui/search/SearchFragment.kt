package com.musify.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.musify.databinding.FragmentSearchBinding
import com.musify.ui.home.TrackAdapter
import com.musify.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var resultsAdapter: TrackAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupSearch()
        observeViewModel()
    }

    private fun setupAdapters() {
        val onTrackClick = { track: com.musify.data.model.Track ->
            startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("TRACK_ID", track.id); putExtra("TRACK_TITLE", track.title)
                putExtra("TRACK_ARTIST", track.artist); putExtra("TRACK_URL", track.previewUrl)
                putExtra("TRACK_ART", track.albumArtUrl)
            })
        }
        resultsAdapter = TrackAdapter(onTrackClick)
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = resultsAdapter

        historyAdapter = SearchHistoryAdapter(
            onQueryClick = { query ->
                binding.etSearch.setText(query)
                viewModel.search(query)
            },
            onRemoveClick = { query -> viewModel.removeSearchHistory(query) }
        )
        binding.rvRecentSearches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentSearches.adapter = historyAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString().trim()
                if (q.isEmpty()) showBrowseState() else viewModel.search(q)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = binding.etSearch.text.toString().trim()
                if (q.isNotEmpty()) viewModel.search(q)
                true
            } else false
        }
    }

    private fun showBrowseState() {
        binding.scrollBrowse.visibility = View.VISIBLE
        binding.rvResults.visibility = View.GONE
        binding.tabLayout.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.results.observe(viewLifecycleOwner) { tracks ->
            resultsAdapter.submitList(tracks)
            binding.rvResults.visibility = View.VISIBLE
            binding.scrollBrowse.visibility = View.GONE
        }
        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
            val hasHistory = history.isNotEmpty()
            binding.tvRecentLabel.visibility = if (hasHistory) View.VISIBLE else View.GONE
            binding.rvRecentSearches.visibility = if (hasHistory) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
