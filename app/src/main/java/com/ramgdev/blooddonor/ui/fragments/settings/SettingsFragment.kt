package com.ramgdev.blooddonor.ui.fragments.settings

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ramgdev.blooddonor.databinding.FragmentSettingsBinding
import com.ramgdev.blooddonor.model.Donor
import com.ramgdev.blooddonor.util.storageReference
import com.ramgdev.blooddonor.util.userCollectionRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@SuppressLint("NewApi")
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var userId: String
    private lateinit var user: FirebaseUser
    private var image: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        updateProfile("myProfile")
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("users")
        user = FirebaseAuth.getInstance().currentUser!!
        userId = user.uid

        binding.editTextView.setOnClickListener {
            binding.topCardView.visibility = INVISIBLE
            binding.editCardView.visibility = VISIBLE
        }

        binding.saveTextView.setOnClickListener {
            binding.topCardView.visibility = VISIBLE

            val fName = binding.editUserName1.text.toString()
            val lName = binding.editUserName2.text.toString()
            val phone = binding.editPhoneNum.text.toString()
            val email = binding.userEmail.text.toString()
            val donor = Donor(fName, lName, email, phone, "")

            editDonorDetails(donor)
            editDonorProfile("myProfile")

            binding.editCardView.visibility = INVISIBLE
            updateProfile("myProfile")

        }

        binding.cancelTextView.setOnClickListener {
            binding.topCardView.visibility = VISIBLE
            binding.editCardView.visibility = INVISIBLE
        }

        binding.selectProfileImage.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    private fun editDonorDetails(donor: Donor) = CoroutineScope(Dispatchers.IO).launch {
        userCollectionRef.add(donor).await()
        withContext(Dispatchers.Main) {
            binding.userName.text = ""
            binding.phoneNum.text = ""
            binding.userEmail.text = ""
        }

    }

    private fun updateProfile(filename: String)  = CoroutineScope(Dispatchers.IO).launch {
        try {

            // Update the profile image
            val maxDownloadSize = 5L * 1024 * 1024
            val bytes = storageReference.child("images/$filename").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            // Retrieve the user's details
            val querySnapshot = userCollectionRef.get().await()
            var donor: Donor? = null
            val name = StringBuilder()
            val email = StringBuilder()
            val phone = StringBuilder()
            for (document in querySnapshot.documents) {
                donor = document.toObject<Donor>()
            }
            name.append(donor?.firstName+ "   "+ donor?.lastName)
            email.append(donor?.email)
            phone.append(donor?.phoneNumber)

            withContext(Dispatchers.Main) {
                binding.profileImage.setImageBitmap(bmp)
                binding.userName.text = name.toString()
                binding.userEmail.text = email.toString()
                binding.phoneNum.text = phone.toString()
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImage() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            startActivityForResult(it, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            data?.data.let {
                image = data?.data
                binding.editImageView.setImageURI(image)
            }
        }
    }

    private fun editDonorProfile(fileName: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            image?.let {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = VISIBLE
                }
                storageReference.child("images/$fileName").putFile(it).await()
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = INVISIBLE
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = INVISIBLE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
   /* override fun onMenuItemClick(item: MenuItem?): Boolean {
        return if (item?.itemId === R.id.action_mode_menu) {
            val sharedPreferences = requireActivity().getSharedPreferences("ui_mode", Context.MODE_PRIVATE)
            val itemUIMode = sharedPreferences.getBoolean("ISCHECKED", false)
            setUIMode(!itemUIMode)
            true
        } else false
    }

    private fun setUIMode(isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveToSharedPrefs(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveToSharedPrefs(false);
        }
    }

    private fun saveToSharedPrefs(isChecked: Boolean) {
        val sharedPreferences = activity?.getSharedPreferences("ui_mode", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("ISCHECKED", isChecked)
        editor?.apply()
    }*/

}