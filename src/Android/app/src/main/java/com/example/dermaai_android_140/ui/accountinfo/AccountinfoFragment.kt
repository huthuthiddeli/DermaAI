package com.example.dermaai_android_140.ui.accountinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.R


class AccountinfoFragment(private val isLogin : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(isLogin)
        {
            return inflater.inflate(R.layout.fragment_accountinfo_login, container, false)
        }
        else{
            return inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        }

    }
}
