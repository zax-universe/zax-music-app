package com.musify.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.musify.R
import com.musify.databinding.FragmentLoginBinding
import com.musify.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { requireActivity().finish() }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "Please fill in all fields"
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            viewModel.login(email, password)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !loading
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.tvError.text = error
            binding.tvError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
