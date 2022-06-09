package com.ramgdev.blooddonor.ui.fragments.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentScreenTwoBinding

class ScreenTwoFragment : Fragment() {

    private lateinit var button: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_screen_two, container, false)

        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPagerFragment)
        button = view.findViewById(R.id.btnScreenTwo)
        button.setOnClickListener {
            viewPager.currentItem = 2
        }

        return view
    }
}