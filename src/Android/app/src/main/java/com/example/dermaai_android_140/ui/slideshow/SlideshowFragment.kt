package com.example.dermaai_android_140.ui.slideshow

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.databinding.FragmentSlideshowBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraX
import androidx.camera.core.impl.ConfigProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.dermaai_android_140.R

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //
        val takePhotoBtn: Button = binding.takePhotoBtn


        //launcher
        var takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Get the URI of the photo taken
                    val imageUri: Uri? = result.data?.data
                    if (imageUri != null) {
                        var imageView = binding.takenPhoto
                        imageView.setImageURI(imageUri) // Show
                    }
                }
            }

        takePhotoBtn.setOnClickListener({
            openCamera(takePictureLauncher)
            }
        )
        //

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //
    private fun openCamera(takePictureLauncher : ActivityResultLauncher<Intent>) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission(takePictureLauncher)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(intent)

        }
    }


    private fun requestCameraPermission(takePictureLauncher : ActivityResultLauncher<Intent>) {

        val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    
    //
}