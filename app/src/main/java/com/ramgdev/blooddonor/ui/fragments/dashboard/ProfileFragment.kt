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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.ramgdev.blooddonor.databinding.FragmentProfileBinding
import com.ramgdev.blooddonor.model.Appointment
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

    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance().reference
        val firebaseMessaging = FirebaseMessaging.getInstance()

//        firebaseMessaging.subscribeToTopic("new_appointment")

        loadProfile("myProfile")
        binding.mapView.getMapAsync { map ->
            this.map = map
        }
        binding.mapView.onCreate(savedInstanceState)

        binding.datePicker.setOnClickListener {
            bookAppointment()
        }

        binding.button.setOnClickListener {
            val date = binding.bookedDate.text.toString()
            val time = binding.bookedTime.text.toString()

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(requireContext(), "Please Select Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userDataMap = HashMap<String, String>()
            userDataMap["name"] = date
            userDataMap["amount"] = time

            FirebaseDatabase.getInstance().getReference("appointment").push().setValue(userDataMap)
            binding.bookedDate.text = ""
            binding.bookedTime.text = ""

            Toast.makeText(requireContext(), "Appointment booked successfully", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun bookAppointment() {
        binding.datePicker.setOnClickListener {

            // Get Current Time
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val myFormat = "HH:mm"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                        binding.bookedTime.text = sdf.format(calendar.time).toEditable()
                    } else {
                        binding.bookedTime.text = "${sdf.format(calendar.time)} PM".toEditable()
                    }
                    binding.bookedTime.text = sdf.format(calendar.time).toEditable()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

            // Get Current Date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "MM/dd/yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding.bookedDate.text = sdf.format(calendar.time).toEditable()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()

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
            for (document in querySnapshot.documents) {
                donor = document.toObject<Donor>()
            }
            name.append(donor?.firstName+ "   "+ donor?.lastName)
            phone.append(donor?.phoneNumber)
            withContext(Dispatchers.Main) {
                binding.userProfileImage.setImageBitmap(bmp)
                binding.donorName.text = name.toString()
                binding.donorContact.text = phone.toString()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun show(applicationContext: Context, tag: String) {}

}