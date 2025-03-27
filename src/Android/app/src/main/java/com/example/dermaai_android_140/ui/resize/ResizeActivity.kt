package com.example.dermaai_android_140.ui.resize

import android.app.ActivityOptions
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.myClasses.Diagnosis
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.Storage
import com.example.dermaai_android_140.ui.camera.CameraViewModel
import com.example.dermaai_android_140.ui.home.HomeFragment
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ResizeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResizeBinding
    private lateinit var base64: String
    private lateinit var imageUriString: String


    @OptIn(ExperimentalEncodingApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityResizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resizeViewModel = ViewModelProvider(this).get(ResizeViewModel::class.java)

        resizeViewModel.setCurrentUser()


        imageUriString = intent.getStringExtra("image_uri").toString()
        val modelIndex = intent.getIntExtra("modelIndex", -1)
        val framework : String = intent.getStringExtra("framework").toString()
        val url : String = intent.getStringExtra("url").toString()



        val imageFile = File(imageUriString)
        val imageBytes = imageFile.readBytes()

        base64 = Base64.encode(imageBytes)


       //val imageUri = imageUriString.toUri()
        val imageUri = Uri.fromFile(File(imageUriString))

        loadImageIntoImageView(imageUri)
        

        resizeViewModel.error.observe(this) { error ->
            Toast.makeText(baseContext, error, Toast.LENGTH_SHORT).show()
        }

        resizeViewModel.resizedImage.observe(this) { resizedImage ->

            if(resizedImage != null)
            {
                base64 = resizedImage.image
                loadBase64ImageIntoImageView(resizedImage.image)
            }
        }

        val resizeViaAiBtn = findViewById<Button>(R.id.resizeAiBtn)
        val acceptAndSendBtn = findViewById<Button>(R.id.acceptBtn)

        acceptAndSendBtn.setOnClickListener{
            resizeViewModel.sendImage(url, modelIndex, framework, base64, imageUriString)
        }

        resizeViewModel.prediction.observe(this) { prediction ->

            // Save Prediction to JSON
            val diagnosis = Diagnosis(prediction!!.getPredictionMap(), imageUriString)

            Storage.saveDiagnosis(this, diagnosis, resizeViewModel.getCurrentUser()!!.email)

            savePrediction(resizeViewModel, prediction)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            
        }


        resizeViaAiBtn.setOnClickListener{
            val urlResize = getString(R.string.main) + getString(R.string.image_controller_gateway) + getString(R.string.resize)
            resizeViewModel.resizeImage(urlResize, base64)
        }
    }


    private fun savePrediction(resizeViewModel: ResizeViewModel, prediction : Prediction)
    {
        val url = getString(R.string.main) +
                getString(R.string.user_controller_gateway) +
                getString(R.string.savePrediction_gateway)

        val user = resizeViewModel.getCurrentUser()!!

        val model = PredictionImage(user.email,user.password,base64,prediction.getPredictionMap())

        resizeViewModel.savePrediction(url,model)
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


    @OptIn(ExperimentalEncodingApi::class)
    private fun loadBase64ImageIntoImageView(base64Image: String) {
        try {
            // Decode the base64 string into a ByteArray

            val imageBytes = Base64.decode(base64Image)

            // Decode the ByteArray into a Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Set the Bitmap to the ImageView
            val imageView = findViewById<ImageView>(R.id.fullscreen_image)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load resized image", Toast.LENGTH_SHORT).show()
        }
    }


}

