package com.example.dermaai_android_140.ui.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.MyClasses.Storage
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentGalleryBinding
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding

class TakePhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 1
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (connect to the XML layout)
        val view = inflater.inflate(R.layout.fragment_photo, container, false)

        val takePhotoBtn: Button = binding.takePhotoBtn
        takePhotoBtn.setOnClickListener {

            takePhotoBtn.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
                }
            }

        }

        return view
    }


    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val bitmap = data?.extras?.get("data") as Bitmap

            val storage = Storage()

            storage.saveFileToStorage(bitmap, requireActivity(), requireContext())

        }
    }

    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}