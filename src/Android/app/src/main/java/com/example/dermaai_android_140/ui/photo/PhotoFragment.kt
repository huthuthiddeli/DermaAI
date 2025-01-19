@file:Suppress("DEPRECATION")

package com.example.dermaai_android_140.ui.photo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import com.example.dermaai_android_140.ui.camera.CameraActivity
import java.io.File

class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 1


    private lateinit var photoFile: File


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val photoViewModel =
            ViewModelProvider(this)[PhotoViewModel::class.java]


        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //



        val takePhotoBtn: Button = binding.takePhotoBtn

        takePhotoBtn.setOnClickListener {
            
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //openCamera(photoViewModel)

                val intent = Intent(requireContext(), CameraActivity::class.java)

                startActivity(intent)


            }else{
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
                photoViewModel.requestCameraPermission()
            }
        }
        photoViewModel.requestCameraPermission.observe(viewLifecycleOwner) { requestCount ->
            if (requestCount == 5) {
                showPermissionSettingsDialog()
                photoViewModel.resetCameraPermissionRequest()
            }
        }

        return root
    }



    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Camera permission is required to take photos. Enable it in the app settings.")
            .setPositiveButton("Go to Settings")
            { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    /*
    private fun openCamera(photoViewModel : PhotoViewModel) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // if Camera app exists
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {

            val storage = Storage()
            photoFile = storage.createUniqueImagePath(requireActivity(), true)
            photoViewModel.setTmpImage(photoFile)


            photoFile.also {
                val photoURI : Uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".fileprovider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
        
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        val photoViewModel =
            ViewModelProvider(this)[PhotoViewModel::class.java]



        photoViewModel.currentImage.observe(viewLifecycleOwner) { currentImage ->
            photoViewModel.sendImage(currentImage)
        }

        photoViewModel.setCurrentImage(photoFile)

        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val storage = Storage()

        storage.saveFileToStorage(bitmap, requireContext(),photoFile.absolutePath)


    }
    */



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    
    //
}