package com.ramgdev.blooddonor.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ramgdev.blooddonor.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }
}