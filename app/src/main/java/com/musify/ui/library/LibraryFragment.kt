package com.musify.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.musify.R
import com.musify.databinding.FragmentLibraryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter(
            onClick = { playlist ->
                findNavController().navigate(
                    R.id.action_library_to_playlist_detail,
                    android.os.Bundle().apply { putLong("playlistId", playlist.id) }
                )
            },
            onMore = { playlist -> showPlaylistOptions(playlist) }
        )
        binding.rvLibrary.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLibrary.adapter = adapter

        binding.btnAdd.setOnClickListener { showCreatePlaylistDialog() }
        binding.btnCreatePlaylist.setOnClickListener { showCreatePlaylistDialog() }

        binding.chipPlaylists.setOnCheckedChangeListener { _, checked -> if (checked) viewModel.filterPlaylists() }
        binding.chipDownloaded.setOnCheckedChangeListener { _, checked ->
            if (checked) findNavController().navigate(R.id.action_library_to_downloads)
        }
        binding.chipHistory.setOnCheckedChangeListener { _, checked ->
            if (checked) findNavController().navigate(R.id.action_library_to_history)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
            binding.layoutEmpty.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
            binding.rvLibrary.visibility = if (playlists.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showCreatePlaylistDialog() {
        val input = TextInputEditText(requireContext()).apply { hint = getString(R.string.playlist_name_hint) }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.new_playlist))
            .setView(input)
            .setPositiveButton(getString(R.string.create)) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) viewModel.createPlaylist(name)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showPlaylistOptions(playlist: com.musify.data.local.entity.PlaylistEntity) {
        val options = arrayOf("Delete")
        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                if (which == 0) viewModel.deletePlaylist(playlist)
            }.show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
