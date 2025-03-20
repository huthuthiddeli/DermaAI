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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        checkIfAdmin(homeViewModel)

        return root
    }


    private fun checkIfAdmin(homeViewModel: HomeViewModel)
    {
        homeViewModel.isAdmin.observe(viewLifecycleOwner) { isAdmin ->
            if(isAdmin)
            {
                activity?.invalidateOptionsMenu()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_main_drawer, menu)

        // Set the visibility of the admin menu item
        val adminMenuItem = menu.findItem(R.id.nav_admin)
        adminMenuItem.isVisible = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}