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
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.databinding.ActivityCameraBinding
import com.example.dermaai_android_140.myClasses.Storage

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun takePhoto() {
        // Ensure imageCapture is initialized
        if (!::imageCapture.isInitialized) {
            Log.e("TAG", "ImageCapture not initialized")
            return
        }

        val storage = Storage()
        val file = storage.createUniqueImagePath(this, true)
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


                    val intent = Intent(baseContext, MainActivity::class.java)
                    //intent.putExtra("image_uri", output.savedUri.toString())
                    startActivity(intent)

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