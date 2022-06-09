package com.ramgdev.blooddonor.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.logInButton.setOnClickListener {
            logInWithEmailAndPassword()
        }

        binding.forgotPasswordTV.setOnClickListener {
            val forgotPassword = ForgotPasswordFragment()
            forgotPassword.show(childFragmentManager, "dialog_reset_password")
        }

        binding.signUpTV.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment3)
        }

        return binding.root
    }

    //Login with email and password
    private fun logInWithEmailAndPassword() {
        val email: String = binding.emailLogInET.editText?.text.toString()
        val password: String = binding.passwordLogInET.editText?.text.toString()

        when {
            binding.emailLogInET.editText?.text.toString().isEmpty() -> {
                binding.emailLogInET.editText?.error = "Email required"
            }
            binding.passwordLogInET.editText?.text.toString().isEmpty() -> {
                binding.passwordLogInET.editText?.error = "Password required"
            }
            else -> {
                binding.loginProgressBar.visibility = VISIBLE
                binding.logInButton.isEnabled = false

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            firebaseAuth.signInWithEmailAndPassword(email, password).await()

                            withContext(Dispatchers.Main) {
                                binding.loginProgressBar.visibility = GONE
                                binding.logInButton.isEnabled = true
                                binding.emailLogInET.editText?.setText("")
                                binding.passwordLogInET.editText?.setText("")

                                val firebaseUser = firebaseAuth.currentUser
                                if (firebaseUser!!.isEmailVerified) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Logged in Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please verify your email first",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                                binding.loginProgressBar.visibility = GONE
                                binding.logInButton.isEnabled = true
                                binding.emailLogInET.editText?.setText("")
                                binding.passwordLogInET.editText?.setText("")
                            }
                        }
                    }
                }
            }
        }

    }
}