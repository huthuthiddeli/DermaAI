package com.example.dermaai_android_140.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentHomeBinding
import com.example.dermaai_android_140.myClasses.User

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        //getTestUser(homeViewModel)

        //homeViewModel.checkIfAdmin()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //checkIfAdmin(homeViewModel)

        return root
    }










    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}