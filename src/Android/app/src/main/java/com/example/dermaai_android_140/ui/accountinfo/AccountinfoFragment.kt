package com.example.dermaai_android_140.ui.accountinfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.MyClasses.LoginApi
import com.example.dermaai_android_140.MyClasses.User
import com.example.dermaai_android_140.R


class AccountinfoFragment(private val isLogin : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View? = null

        if (isLogin) {
            view = inflater.inflate(R.layout.fragment_accountinfo_login, container, false)
        } else {
            view = inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        }

        //val viewRegister = inflater.inflate(R.layout.fragment_accountinfo_register, container, false)

        if (isLogin) {
            // Set up login view buttons
            val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
            val loginBtn = view.findViewById<Button>(R.id.loginBtn)

            switchToRegisterBtn.setOnClickListener {
                // Replace the fragment with the register view
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(false))
                    .commit()
            }

            val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
            val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)

            loginBtn.setOnClickListener {

                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                val loginApi = LoginApi()
                val user: User? = loginApi.login(email, password)
                
                if (user != null) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_LONG).show()
                }

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
            
        } else {
            // Set up register view buttons
            val switchToLoginBtn = view.findViewById<Button>(R.id.switchToLoginBtn)

            switchToLoginBtn.setOnClickListener {
                // Replace the fragment with the login view
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(true))
                    .commit()
            }
        }



        return view
    }

}
