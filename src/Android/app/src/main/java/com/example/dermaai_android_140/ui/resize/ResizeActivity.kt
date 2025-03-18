package com.example.dermaai_android_140.ui.resize

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.databinding.ActivityResizeBinding
import com.example.dermaai_android_140.R
import android.widget.Button
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.ui.camera.CameraViewModel

class ResizeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityResizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra("image_uri")
        val modelIndex = intent.getIntExtra("modelIndex", -1)
        val framework : String = intent.getStringExtra("framework").toString()
        val base64 : String = intent.getStringExtra("base64").toString()
        val url : String = intent.getStringExtra("url").toString()

        val imageUri = imageUriString?.toUri()

        if (imageUri != null) {
            loadImageIntoImageView(imageUri)
        }




        val resizeViewModel = ViewModelProvider(this).get(ResizeViewModel::class.java)

        resizeViewModel.error.observe(this) { error ->
            Toast.makeText(baseContext, error, Toast.LENGTH_SHORT).show()
        }

        val resizeViaAiBtn = findViewById<Button>(R.id.resizeAiBtn)
        val acceptAndSendBtn = findViewById<Button>(R.id.acceptBtn)

        acceptAndSendBtn.setOnClickListener{
            resizeViewModel.sendImage(url, modelIndex, framework, base64, imageUriString)
        }

        resizeViaAiBtn.setOnClickListener{
            val urlResize = getString(R.string.main) + getString(R.string.image_controller_gateway) + getString(R.string.resize)
            resizeViewModel.resizeImage(urlResize, base64)
        }


    }

    private fun loadImageIntoImageView(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val imageView = findViewById<ImageView>(R.id.fullscreen_image)
            imageView.setImageBitmap(bitmap)
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}