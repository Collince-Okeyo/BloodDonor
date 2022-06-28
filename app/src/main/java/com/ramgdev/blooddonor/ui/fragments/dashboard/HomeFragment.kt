package com.ramgdev.blooddonor.ui.fragments.dashboard

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentHomeBinding
import com.ramgdev.blooddonor.model.Donor
import com.ramgdev.blooddonor.util.userCollectionRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeFragment : Fragment()  {

    private lateinit var binding: FragmentHomeBinding
    private var storageReference = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.imageViewSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment3_to_settingsFragment)
        }

        val donor = Donor(null, null, null, null, null)
        loadProfile("myImage")
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun loadProfile(fileName: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            // Update the profile image
            val maxDownloadSize = 5L * 1024 * 1024
            val bytes = storageReference.child("images/$fileName").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            // Retrieve the user's details
            val querySnapshot = userCollectionRef.get().await()
            var donor: Donor? = null
            val name = StringBuilder()
            for (document in querySnapshot.documents) {
                donor = document.toObject<Donor>()
            }
            name.append(donor?.firstName)

            withContext(Dispatchers.Main) {
                binding.profileImage.setImageBitmap(bmp)
                binding.profileName.text = name.toString()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        Timber.d("Method Called")
    }
}