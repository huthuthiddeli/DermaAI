package com.example.dermaai_android_140.ui.result

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityResultBinding
import com.example.dermaai_android_140.myClasses.Storage

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH")
        
        val storage = Storage()
        //val predictionString : String = storage.readPredictionFromImageMetadata(imagePath.toString()).toString()

        val predictionString : String = storage.readDiagnosisFromFile(imagePath.toString()).toString()

        val resultView = findViewById<TextView>(R.id.result_text)
        resultView.text = predictionString


        val bitmap = BitmapFactory.decodeFile(imagePath)
        binding.fullscreenImage.setImageBitmap(bitmap)



    }
}