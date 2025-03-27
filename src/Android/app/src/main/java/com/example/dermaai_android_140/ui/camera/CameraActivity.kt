package com.example.dermaai_android_140.ui.camera

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityCameraBinding
import com.example.dermaai_android_140.myClasses.Storage
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.example.dermaai_android_140.ui.resize.ResizeActivity

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraViewModel: CameraViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedIndex = intent.getIntExtra("SELECTED_INDEX", -1)
        val selectedFramework = intent.getStringExtra("SELECTED_FRAMEWORK").toString()

        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        cameraViewModel.setCurrentUser()

        cameraViewModel.setModelIndex(selectedIndex)
        cameraViewModel.setFramework(selectedFramework)

        cameraViewModel.error.observe(this) { error ->
            Toast.makeText(baseContext, error, Toast.LENGTH_SHORT).show()
        }


        val previewView: PreviewView = binding.previewView

        // Initialize the camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Bind the preview and image capture use cases
            bindPreviewAndLifecycle(cameraProvider, previewView)

        }, ContextCompat.getMainExecutor(this))

        // Set up the shoot photo button click listener
        binding.shootPhotoBtn.setOnClickListener {
            takePhoto()
        }
    }


    @OptIn(ExperimentalEncodingApi::class)
    private fun takePhoto() {
        // Ensure imageCapture is initialized
        if (!::imageCapture.isInitialized) {
            Log.e("TAG", "ImageCapture not initialized")
            return
        }

        val file = Storage.createUniqueImagePath(this, cameraViewModel.getCurrentUser()!!.email)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }


                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)


                    val storage = Storage()

                    var base64 : String? = null

                    val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.model_predict_gateway)

                    if(output.savedUri != null)
                    {

                        val imageFile = File(output.savedUri!!.path)
                        val imageBytes = imageFile.readBytes()

                        base64 = Base64.encode(imageBytes)

                        // Save Image

                        //

                        //cameraViewModel.sendImage(url, cameraViewModel.getModelIndex(), cameraViewModel.getFramework(), base64,output.savedUri!!.path)

                    }

                    // Start Resize Activity


                    val modelIndex = cameraViewModel.getModelIndex()
                    val framework = cameraViewModel.getFramework()
                    val imageUri = output.savedUri!!.path

                    val intent = Intent(baseContext, ResizeActivity::class.java).apply{

                        putExtra("url", url)
                        putExtra("modelIndex", modelIndex)
                        putExtra("framework", framework)
                        putExtra("image_uri", imageUri)

                    }

                    startActivity(intent)

                    //val intent = Intent(baseContext, MainActivity::class.java)
                    // //intent.putExtra("image_uri", output.savedUri.toString())
                    //startActivity(intent)

                }
            }
        )
    }

    private fun bindPreviewAndLifecycle(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        // Set up the preview use case
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        // Set up the image capture use case
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        // Select the back camera as the default
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        
        try {
            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()

            // Bind the camera to the lifecycle
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageCapture
            )

        } catch (exc: Exception) {
            Log.e("TAG", "Use case binding failed", exc)
        }
    }
}