package com.example.dermaai_android_140.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentLoginBinding
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoFragment
import com.example.dermaai_android_140.ui.register.RegisterFragment

class LoginFragment() : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
        val loginBtn = view.findViewById<Button>(R.id.loginBtn)

        switchToRegisterBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_buttons, RegisterFragment())
                .addToBackStack(null)
                .commit()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(false))
                .commit()
        }

        loginBtn.setOnClickListener{
            
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }





}