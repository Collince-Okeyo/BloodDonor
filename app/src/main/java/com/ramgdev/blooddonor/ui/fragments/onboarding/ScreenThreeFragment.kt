package com.ramgdev.blooddonor.ui.fragments.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentSCreenThreeBinding

class ScreenThreeFragment : Fragment() {

    private lateinit var binding: FragmentSCreenThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSCreenThreeBinding.inflate(inflater, container, false)

        return binding.root
    }
}