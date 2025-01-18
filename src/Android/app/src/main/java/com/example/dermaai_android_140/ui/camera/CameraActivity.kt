package com.example.dermaai_android_140.ui.camera


import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityCameraBinding
import java.text.SimpleDateFormat
import java.util.Locale


class CameraActivity : AppCompatActivity() {



    private lateinit var binding: ActivityCameraBinding

    private lateinit var imageCapture: ImageCapture


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_camera)

        val previewView: PreviewView = binding.previewView
        var cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        previewView.controller = cameraController




        val preview = Preview.Builder().build()
        val viewFinder: PreviewView = findViewById(R.id.previewView)


        // PreviewView creates a surface provider and is the recommended provider
        preview.surfaceProvider = viewFinder.surfaceProvider

        // The use case is bound to an Android Lifecycle with the following code
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        cameraProviderFuture.addListener(Runnable {

            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            bindPreviewAndLifecycle(cameraProvider, viewFinder)

            // Connect the preview use case to the previewView
            // preview.surfaceProvider = previewView.surfaceProvider

        }, ContextCompat.getMainExecutor(this))

    }


    private fun bindPreviewAndLifecycle(cameraProvider : ProcessCameraProvider, viewFinder : PreviewView)
    {
        // Set up the preview use case to display camera preview.
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = viewFinder.surfaceProvider
        }

        // Set up the capture use case to allow users to take photos.
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        // Choose the camera by requiring a lens facing
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()


        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Attach use cases to the camera with the same lifecycle owner
            val camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview)


            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview)

        } catch(exc: Exception) {
            Log.e("TAG", "Use case binding failed", exc)
        }


        createFile()

        receiveImage()



    }


    private fun createFile()
    {
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
    }
    

    private fun receiveImage()
    {

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }






}