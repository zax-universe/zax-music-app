package com.musify.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.musify.R
import com.musify.databinding.FragmentSettingsBinding
import com.musify.ui.auth.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun observeViewModel() {
        viewModel.username.observe(viewLifecycleOwner) { binding.tvUsername.text = it }
        viewModel.email.observe(viewLifecycleOwner) { binding.tvEmail.text = it }
        viewModel.streamingQuality.observe(viewLifecycleOwner) { binding.tvStreamingQuality.text = it }
        viewModel.downloadQuality.observe(viewLifecycleOwner) { binding.tvDownloadQuality.text = it }
        viewModel.cacheSize.observe(viewLifecycleOwner) { binding.tvCacheSize.text = it }
        viewModel.logoutEvent.observe(viewLifecycleOwner) { shouldLogout ->
            if (shouldLogout) navigateToWelcome()
        }
    }

    private fun setupClickListeners() {
        val qualities = arrayOf(
            getString(R.string.quality_low),
            getString(R.string.quality_normal),
            getString(R.string.quality_high),
            getString(R.string.quality_very_high)
        )

        binding.itemStreamingQuality.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.streaming_quality))
                .setItems(qualities) { _, which -> viewModel.setStreamingQuality(qualities[which]) }
                .show()
        }

        binding.itemDownloadQuality.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.download_quality))
                .setItems(qualities) { _, which -> viewModel.setDownloadQuality(qualities[which]) }
                .show()
        }

        binding.itemClearCache.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.clear_cache))
                .setMessage("Clear all cached data?")
                .setPositiveButton(getString(R.string.ok)) { _, _ -> viewModel.clearCache() }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }

        binding.itemLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.logout))
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(getString(R.string.logout)) { _, _ -> viewModel.logout() }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    private fun navigateToWelcome() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
