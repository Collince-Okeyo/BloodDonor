package com.ramgdev.blooddonor.ui.fragments.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.firestore.ktx.toObject
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentProfileBinding
import com.ramgdev.blooddonor.model.Donor
import com.ramgdev.blooddonor.util.ToEditable
import com.ramgdev.blooddonor.util.storageReference
import com.ramgdev.blooddonor.util.userCollectionRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment(), OnMapReadyCallback, ToEditable {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var map: GoogleMap
    var calendar: Calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadProfile("myProfile")
        binding.mapView.getMapAsync { map ->
            this.map = map
        }
        binding.mapView.onCreate(savedInstanceState)

        binding.datePicker.setOnClickListener {
            bookDonationDate()
        }

        binding.button.setOnClickListener {
            val date = binding.date.text.toString()
            val time = binding.time.text.toString()
            val donor = Donor(date, time)
            donationSchedule(donor)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun bookDonationDate() {

        // Get Current Time
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val myFormat = "HH:mm"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                    binding.time.text = sdf.format(calendar.time).toEditable()
                } else {
                    binding.time.text = "${sdf.format(calendar.time)} PM".toEditable()
                }
                binding.time.text = sdf.format(calendar.time).toEditable()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()

        timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)


        // Get current date
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val format = "MMM dd, yyyy"
                val sdf = SimpleDateFormat(format, Locale.US)
                binding.date.text = sdf.format(calendar.time).toEditable()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    }

    // date and time
    private fun donationSchedule(donor: Donor) = CoroutineScope(Dispatchers.IO).launch {
        userCollectionRef.add(donor).await()
        withContext(Dispatchers.Main) {
            binding.date.text = ""
            binding.time.text = ""
        }
    }


    // Use profile
    @SuppressLint("SetTextI18n")
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
            val phone = StringBuilder()
            val bloodG = StringBuilder()
            for (document in querySnapshot.documents) {
                donor = document.toObject<Donor>()
            }
            name.append(donor?.firstName+ "   "+ donor?.lastName)
            phone.append(donor?.phoneNumber)
            bloodG.append(donor?.bloodGroup)
            withContext(Dispatchers.Main) {
                binding.userProfileImage.setImageBitmap(bmp)
                binding.donorName.text = name.toString()
                binding.donorContact.text = phone.toString()
                binding.bloodType.text = bloodG.toString()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun show(application: Context, tag: String) {}
}