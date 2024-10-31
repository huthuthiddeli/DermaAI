package com.example.dermaai_android_140.ui.result

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityMainBinding
import com.example.dermaai_android_140.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_result)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("IMAGE_PATH")
        val bitmap = BitmapFactory.decodeFile(imagePath)
        binding.fullscreenImage.setImageBitmap(bitmap)
    }
}