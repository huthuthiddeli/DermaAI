package com.example.dermaai_android_140.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.databinding.ActivityAdminBinding
import kotlin.io.encoding.ExperimentalEncodingApi

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}