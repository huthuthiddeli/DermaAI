package com.example.dermaai_android_140.ui.result

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.ActivityResultBinding
import com.example.dermaai_android_140.myClasses.Storage

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var resultViewModel: ResultViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result)
        
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultViewModel = ViewModelProvider(this).get(ResultViewModel::class.java)




        val imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH")


        val user = resultViewModel.getCurrentUser()!!.email
        val predicitionMap = Storage.readDiagnosisForImage(this, imagePath.toString(), user)

        val resultView = findViewById<TextView>(R.id.result_text)
        resultView.text = formatPredictionSimple(predicitionMap!!)



        val bitmap = BitmapFactory.decodeFile(imagePath)
        binding.fullscreenImage.setImageBitmap(bitmap)

    }




    fun formatPredictionSimple(predictionMap: Map<String, Double>): SpannableString {
        val finalResult = SpannableStringBuilder()

        predictionMap.forEach { (key, value) ->
            val line = "${key.replaceFirstChar { it.uppercase() }}: ${"%.2f%%".format(value)}\n"
            val start = finalResult.length

            finalResult.append(line)
            finalResult.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                start + key.length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Remove line
        if (finalResult.isNotEmpty()) {
            finalResult.delete(finalResult.length - 1, finalResult.length)
        }

        return SpannableString(finalResult)
    }

}