package com.example.dermaai_android_140.ui.stayLoggedIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.ui.login.LoginViewModel


class StayLoggedInFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_stay_logged_in, container, false)

        //val accountInfoViewModel: AccountinfoViewModel by viewModels()

        val loginViewModel: LoginViewModel by activityViewModels()

        val switch : Switch = view.findViewById<Switch>(R.id.stayLoggedInSwitch)


        switch.setOnCheckedChangeListener {_, isChecked ->
            loginViewModel.setStayLoggedIn(isChecked)
            //accountInfoViewModel.setStayLoggedIn(isChecked)
        }
        
        return view


    }
}
