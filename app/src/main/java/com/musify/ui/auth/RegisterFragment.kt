package com.musify.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.musify.databinding.FragmentRegisterBinding
import com.musify.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.tvLogin.setOnClickListener { findNavController().navigateUp() }

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "Please fill in all fields"
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            viewModel.register(username, email, password)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !loading
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.tvError.text = error
            binding.tvError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        viewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
