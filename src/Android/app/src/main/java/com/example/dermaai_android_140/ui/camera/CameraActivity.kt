package com.example.dermaai_android_140.ui.camera

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Storage
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.example.dermaai_android_140.ui.resize.ResizeActivity

class CameraActivity : AppCompatActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var previewView: PreviewView
    private lateinit var shootPhotoBtn: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Initialize views
        previewView = findViewById(R.id.previewView)
        shootPhotoBtn = findViewById(R.id.shootPhotoBtn)

        val selectedIndex = intent.getIntExtra("SELECTED_INDEX", -1)
        val selectedFramework = intent.getStringExtra("SELECTED_FRAMEWORK").toString()

        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        cameraViewModel.setModelIndex(selectedIndex)
        cameraViewModel.setFramework(selectedFramework)

        cameraViewModel.error.observe(this) { error ->
            Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
        }

        // Initialize camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreviewAndLifecycle(cameraProvider, previewView)
        }, ContextCompat.getMainExecutor(this))

        // Set click listener
        shootPhotoBtn.setOnClickListener {
            takePhoto()
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun takePhoto() {
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
                    Toast.makeText(this@CameraActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)

                    val url = getString(R.string.main) +
                            getString(R.string.model_controller_gateway) +
                            getString(R.string.model_predict_gateway)

                    val intent = Intent(this@CameraActivity, ResizeActivity::class.java).apply {
                        putExtra("url", url)
                        putExtra("modelIndex", cameraViewModel.getModelIndex())
                        putExtra("framework", cameraViewModel.getFramework())
                        putExtra("image_uri", output.savedUri?.path)
                    }
                    startActivity(intent)
                }
            }
        )
    }

    private fun bindPreviewAndLifecycle(
        cameraProvider: ProcessCameraProvider,
        previewView: PreviewView
    ) {
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("TAG", "Use case binding failed", exc)
        }
    }
}