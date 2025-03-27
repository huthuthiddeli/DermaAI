package com.example.dermaai_android_140.ui.result

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityResultBinding
import com.example.dermaai_android_140.myClasses.Storage
import com.example.dermaai_android_140.ui.camera.CameraViewModel
import com.example.dermaai_android_140.ui.resize.ResizeViewModel
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var resultViewModel: ResultViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultViewModel = ViewModelProvider(this).get(ResultViewModel::class.java)
        resultViewModel.setCurrentUser()

        val imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH")

        //val predictionString : String = storage.readPredictionFromImageMetadata(imagePath.toString()).toString()
        val user = resultViewModel.getCurrentUser()!!.email
        val predicitionMap = Storage.readDiagnosisForImage(this, imagePath.toString(), user)

        val resultView = findViewById<TextView>(R.id.result_text)
        resultView.text = Gson().toJson(predicitionMap)

        val bitmap = BitmapFactory.decodeFile(imagePath)
        binding.fullscreenImage.setImageBitmap(bitmap)

        
        
    }
}