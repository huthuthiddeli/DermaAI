package com.example.dermaai_android_140.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityMainBinding
import com.example.dermaai_android_140.databinding.FragmentLoginBinding
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoFragment
import com.example.dermaai_android_140.ui.register.RegisterFragment

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        
        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(true))
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_buttons, LoginFragment())
                .commit()

        }




    }

}