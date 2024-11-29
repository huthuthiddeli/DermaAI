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
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.R


class AccountinfoFragment() : Fragment() {

    private var isLogin: Boolean = false

    //val viewModel: AccountinfoViewModel by viewModel()

    companion object {
        fun newInstance(isLogin: Boolean): AccountinfoFragment {
            val fragment = AccountinfoFragment()
            fragment.isLogin = isLogin
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View? = null


        val viewModel = ViewModelProvider(this).get(AccountinfoViewModel::class.java)
        
        if (isLogin) {
            view = inflater.inflate(R.layout.fragment_accountinfo_login, container, false)
        } else {
            view = inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        }

        //val viewRegister = inflater.inflate(R.layout.fragment_accountinfo_register, container, false)




        if (isLogin) {
            val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
            val loginBtn = view.findViewById<Button>(R.id.loginBtn)

            val stayLoggedInContainer = view.findViewById<View>(R.id.stayLoggedInContainer)

            switchToRegisterBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment.newInstance(false))
                    .commit()
            }


            val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
            val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)

            loginBtn.setOnClickListener {

                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                viewModel.login(email, password)

            }

            viewModel.isLoggedIn.observe(viewLifecycleOwner, { isLoggedIn ->
                if (isLoggedIn) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_LONG).show()
                }
            })



            
        } else {

            val switchToLoginBtn = view.findViewById<Button>(R.id.switchToLoginBtn)

            switchToLoginBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment.newInstance(true))
                    .commit()
            }
        }



        return view
    }

}
