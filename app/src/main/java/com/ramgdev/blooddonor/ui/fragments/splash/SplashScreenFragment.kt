package com.ramgdev.blooddonor.ui.fragments.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.ui.activity.DashBoardActivity
import com.ramgdev.blooddonor.util.firebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {

    private val user = firebaseAuth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler().postDelayed({
            if (user != null && onBoardingFinished()){
                startActivity(Intent(activity, DashBoardActivity::class.java))
                activity?.finish()
            } else if (user == null && onBoardingFinished()){
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            } else {
                findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment)
            }
        }, 1500)

        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    private fun onBoardingFinished(): Boolean {
        val sharePreferences =
            requireContext().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharePreferences.getBoolean("Finished", false)
    }

}