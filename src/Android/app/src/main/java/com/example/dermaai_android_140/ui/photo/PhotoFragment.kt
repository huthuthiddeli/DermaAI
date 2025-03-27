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
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.ui.camera.CameraActivity
import java.io.File
import com.example.dermaai_android_140.myClasses.ModelSelectionDialog
import com.example.dermaai_android_140.ui.settings.SettingsViewModel

class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 1


    private lateinit var photoFile: File

    private lateinit var photoViewModel: PhotoViewModel


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        photoViewModel = ViewModelProvider(this)[PhotoViewModel::class.java]


        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //



        val takePhotoBtn: Button = binding.takePhotoBtn

        takePhotoBtn.setOnClickListener {
            
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //openCamera(photoViewModel)

                getModels()

                //val intent = Intent(requireContext(), CameraActivity::class.java)
                //startActivity(intent)

                
            }else{
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
                photoViewModel.requestCameraPermission()
            }
        }


        setUpObserver()

        return root
    }


    private fun getModels() {
        val photoViewModel =
            ViewModelProvider(this)[PhotoViewModel::class.java]

        val url =
            getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(
                R.string.getModels_gateway
            )

        photoViewModel.getModels(url)
    }



    private fun setUpObserver()
    {
        photoViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        photoViewModel.requestCameraPermission.observe(viewLifecycleOwner) { requestCount ->
            if (requestCount == 5) {
                showPermissionSettingsDialog()
                photoViewModel.resetCameraPermissionRequest()
            }
        }

        photoViewModel.models.observe(viewLifecycleOwner) { modelTrainer ->

            if(modelTrainer != null)
            {
                showModelSelectionDialog(modelTrainer)
            }
        }
    }


    private fun showModelSelectionDialog(modelTrainer: ModelTrainer) {
        ModelSelectionDialog(
            requireContext(),
            modelTrainer.ModelTrainerPyTorch,
            modelTrainer.ModelTrainerSKLearn,
            modelTrainer.ModelTrainerTensorFlow
        ) { framework, model ->
            // 6. Use the correct ViewModel reference
            val photoViewModel = ViewModelProvider(this)[PhotoViewModel::class.java]


            var models = photoViewModel.models.value
            var index : Int? = null

            var frameworkToSend : String = ""

            if(framework.equals("PyTorch"))
            {
                frameworkToSend = "ModelTrainerPyTorch"
                for(mdl in models?.getPyTorch()!!)
                {
                    if(mdl.equals(model)) {
                        index = models?.getPyTorch()?.indexOf(mdl)
                    }
                }
            }
            else if(framework.equals("Scikit-Learn"))
            {
                frameworkToSend = "ModelTrainerSKLearn"

                for(mdl in models?.getSKLearn()!!)
                {
                    if(mdl.equals(model)) {
                        index = models?.getSKLearn()?.indexOf(mdl)
                    }
                }
            }
            else if(framework.equals("TensorFlow"))
            {
                frameworkToSend = "ModelTrainerTensorFlow"
                for(mdl in models?.getTensorFlow()!!)
                {
                    if(mdl.equals(model)) {
                        index = models?.getTensorFlow()?.indexOf(mdl)
                    }
                }
            }

            try {
                val intent = Intent(requireContext(), CameraActivity::class.java)
                intent.putExtra("SELECTED_INDEX", index)
                intent.putExtra("SELECTED_FRAMEWORK", frameworkToSend)
                startActivity(intent)
            }catch (ex : Exception)
            {
                ex.printStackTrace()
            }



        }.show()
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