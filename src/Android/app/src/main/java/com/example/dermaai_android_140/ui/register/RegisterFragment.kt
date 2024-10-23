package com.example.dermaai_android_140.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentLoginBinding
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoFragment
import com.example.dermaai_android_140.ui.login.LoginFragment

class RegisterFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)


        val switchToLoginBtn = view.findViewById<Button>(R.id.switchToLoginBtn)
        //switchToRegisterBtn = binding.switchToRegisterBtn


        switchToLoginBtn.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_buttons, LoginFragment())
                .addToBackStack(null)
                .commit()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment(true))
                .commit()
            
        }


        return view
    }


}