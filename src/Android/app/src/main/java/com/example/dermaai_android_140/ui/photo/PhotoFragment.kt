package com.example.dermaai_android_140.ui.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.myClasses.Storage
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import java.io.File

class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 1


    private lateinit var photoFile: File
    private lateinit var photoURI: Uri



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val photoViewModel =
            ViewModelProvider(this).get(PhotoViewModel::class.java)

        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        photoViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //
        val takePhotoBtn: Button = binding.takePhotoBtn


        takePhotoBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }
        
        return root
    }
    

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // if Camera app exists
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val storage = Storage()
            photoFile = storage.createUniqueImagePath(requireActivity(), true)
            photoFile.also {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    requireActivity().packageName + ".fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            val storage = Storage()
            storage.saveFileToStorage(bitmap, requireActivity(), requireContext(), true)
        }
    }


    //




    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    
    //
}