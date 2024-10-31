package com.example.dermaai_android_140.ui.accountinfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.R


class AccountinfoFragment(private val isLogin : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewRegister = inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        val viewLogin = inflater.inflate(R.layout.fragment_accountinfo_login, container, false)

        // Login

        val switchToRegisterBtn = viewLogin.findViewById<Button>(R.id.switchToRegisterBtn)
        val loginBtn = viewLogin.findViewById<Button>(R.id.loginBtn)

        switchToRegisterBtn.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(false))
                .commit()
        }

        loginBtn.setOnClickListener{

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        //


        // Register

        val switchToLoginBtn = viewRegister.findViewById<Button>(R.id.switchToLoginBtn)
        //switchToRegisterBtn = binding.switchToRegisterBtn

        switchToLoginBtn.setOnClickListener {


            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(true))
                .commit()

        }
        //



        if(isLogin)
        {
            return inflater.inflate(R.layout.fragment_accountinfo_login, container, false)
        }
        else{
            return inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        }
    }

}
