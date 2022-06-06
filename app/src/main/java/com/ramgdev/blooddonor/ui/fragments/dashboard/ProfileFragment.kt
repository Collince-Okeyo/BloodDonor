package com.ramgdev.blooddonor.ui.fragments.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var bindind: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindind = FragmentProfileBinding.inflate(inflater, container, false)

        bindind.toolbarProfile.setNavigationIcon(R.drawable.ic_arrow_back)
        bindind.toolbarProfile.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        bindind.calendarView.setOnClickListener {
            Toast.makeText(requireContext(), "Calendar Clicked", Toast.LENGTH_SHORT).show()
            bindind.calendar.visibility = VISIBLE
        }

        return bindind.root
    }
}