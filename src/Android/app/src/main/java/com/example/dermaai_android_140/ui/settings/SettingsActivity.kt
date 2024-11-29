package com.example.dermaai_android_140.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermaai_android_140.R
import androidx.fragment.app.FragmentTransaction

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        //  back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}