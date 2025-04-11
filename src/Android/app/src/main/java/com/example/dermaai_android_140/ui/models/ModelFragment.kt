package com.example.dermaai_android_140.ui.models

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentModelBinding
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import com.example.dermaai_android_140.ui.photo.PhotoViewModel

class ModelFragment : Fragment() {


    private var _binding: FragmentModelBinding? = null


    private lateinit var modelViewModel: ModelViewModel


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        modelViewModel = ViewModelProvider(this)[ModelViewModel::class.java]
        _binding = FragmentModelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getModelsDescription()

        setUpObserver()

        return root
    }

    private fun setUpObserver()
    {
        modelViewModel = ViewModelProvider(this)[ModelViewModel::class.java]

        modelViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun getModelsDescription()
    {
        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.getModelDescription_gateway)

        modelViewModel = ViewModelProvider(this)[ModelViewModel::class.java]

        modelViewModel.getModelsDescription(url)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}