package com.example.dermaai_android_140.ui.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentTutorialBinding // Import your generated binding class

class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null // Use FragmentTutorialBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false) // Inflate fragment layout

        
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ... your tutorial logic here (e.g., setting text, click listeners) ...
        // Access views using binding.yourViewId (e.g., binding.tutorialTextView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}