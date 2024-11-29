package com.example.dermaai_android_140.ui.login

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityMainBinding
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoFragment


class LoginActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment.newInstance(true))
                .commit()
        }

        //val switch = findViewById<Switch>(R.)

    }


}