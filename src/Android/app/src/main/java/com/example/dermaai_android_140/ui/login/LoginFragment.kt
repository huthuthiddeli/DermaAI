package com.example.dermaai_android_140.ui.login

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentLoginBinding
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import com.example.dermaai_android_140.ui.register.RegisterFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
        //switchToRegisterBtn = binding.switchToRegisterBtn

        switchToRegisterBtn.setOnClickListener {

            val registerFragment = RegisterFragment()
            redirectToPage(registerFragment)
        }

        return view
    }

    private fun redirectToPage(fragment : Fragment)
    {
        childFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .addToBackStack(null)
            .commit()
    }




}