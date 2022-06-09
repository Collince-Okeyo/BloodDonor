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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentRegisterBinding
import com.ramgdev.blooddonor.model.Donor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val userCollectionRef = Firebase.firestore.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            registerWithEmailAndPassword()
        }

        binding.lackAccountTV.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment3_to_loginFragment)
        }

        return binding.root
    }

    private fun registerWithEmailAndPassword() {
        val email = binding.emailSignUpET.editText?.text.toString().trim()
        val password = binding.passwordSignUpET.editText?.text.toString().trim()
        val firstName = binding.firstNameET.editText?.text.toString().trim()
        val lastName = binding.lastNameET.editText?.text.toString().trim()
        val phoneNumber = binding.phoneET.editText?.text.toString().trim()
        val donor = Donor(firstName, lastName, email, phoneNumber, password)


        when {
            binding.firstNameET.editText?.text.toString().isEmpty() -> {
                binding.firstNameET.editText?.error = "Enter First Name"
            }
            binding.lastNameET.editText?.text.toString().isEmpty() -> {
                binding.lastNameET.editText?.error = "Enter Last Name"
            }
            binding.emailSignUpET.editText?.text.toString().isEmpty() -> {
                binding.emailSignUpET.editText?.error = "Enter Email"
            }
            binding.passwordSignUpET.editText?.text.toString().isEmpty() -> {
                binding.passwordSignUpET.editText?.error = "Enter Password"
            }
            binding.phoneET.editText?.text.toString().isEmpty() -> {
                binding.phoneET.editText?.error = "Enter Phone Number"
            }
            binding.passwordSignUpET.editText?.text.toString().length < 8 -> {
                binding.passwordSignUpET.editText?.error = "Short Password"
            }
            else -> {
                binding.progressRegister.visibility = VISIBLE
                binding.registerButton.isEnabled = false

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                            val firebaseUser = firebaseAuth.currentUser
                            firebaseUser!!.sendEmailVerification().await()
                            saveUserDetails(donor)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Account created successfully. Please check your email to verify",
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.firstNameET.editText?.setText("")
                                binding.lastNameET.editText?.setText("")
                                binding.emailSignUpET.editText?.setText("")
                                binding.passwordSignUpET.editText?.setText("")
                                binding.phoneET.editText?.setText("")

                                findNavController().navigate(R.id.action_registerFragment3_to_loginFragment)

                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()

                                binding.progressRegister.visibility = GONE
                                binding.registerButton.isEnabled = true

                                binding.firstNameET.editText?.setText("")
                                binding.lastNameET.editText?.setText("")
                                binding.emailSignUpET.editText?.setText("")
                                binding.passwordSignUpET.editText?.setText("")
                                binding.phoneET.editText?.setText("")
                            }
                        }
                    }
                }
            }
        }
    }

    //saving user details
    private fun saveUserDetails(user: Donor) = CoroutineScope(Dispatchers.IO).launch {
        try {
            userCollectionRef.add(user).await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}