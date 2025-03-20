package com.example.dermaai_android_140.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.databinding.ActivityAdminBinding
import kotlin.io.encoding.ExperimentalEncodingApi
import com.example.dermaai_android_140.R
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.ui.resize.ResizeViewModel

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adminViewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        val retrainAllBtn = findViewById<Button>(R.id.retrainAllBtn)
        val retrainBtn = findViewById<Button>(R.id.retrainBtn)

        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.retrainAll_gateway)

        retrainAllBtn.setOnClickListener{
            retrainAll(adminViewModel)
        }

        retrainBtn.setOnClickListener{
            retrain(adminViewModel)
        }


    }


    private fun retrainAll(adminViewModel : AdminViewModel)
    {
        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.retrainAll_gateway)
        adminViewModel.retrainAll(url)
    }

    private fun retrain(adminViewModel : AdminViewModel){

        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.retrain_gateway)

        adminViewModel.retrain(url)

    }

}