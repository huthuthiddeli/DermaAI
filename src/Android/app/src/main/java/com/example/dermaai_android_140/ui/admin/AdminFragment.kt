package com.example.dermaai_android_140.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityAdminBinding
import com.example.dermaai_android_140.databinding.FragmentAdminBinding

class AdminFragment : Fragment(R.layout.fragment_admin) {

    private lateinit var binding: FragmentAdminBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminBinding.bind(view)

        // Handle any UI logic or listeners here for AdminFragment
    }
}