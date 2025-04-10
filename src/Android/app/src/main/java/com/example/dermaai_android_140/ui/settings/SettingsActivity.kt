package com.example.dermaai_android_140.ui.settings

import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Diagnosis
import com.example.dermaai_android_140.myClasses.Storage

class SettingsActivity : AppCompatActivity() {


    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        settingsViewModel.allPredictions.observe(this) { response ->

            val user = settingsViewModel.getCurrentUser()

            if (response.isNullOrEmpty()) {
                Toast.makeText(this, "No predictions to sync", Toast.LENGTH_SHORT).show()
                return@observe
            }

            try {
                val filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                var imageCount = 0

                val localImages = Storage.retrieveImagesFromStorage(filesDir, user.email)
                val existingBase64 = localImages.mapNotNull {
                    Storage.convertImageToBase64(it)
                }.toSet()

                response.forEach { prediction ->
                    if (!existingBase64.contains(prediction.image)) {
                        val file = Storage.createUniqueImagePath(this, user.email)
                        val newBitmap = Storage.base64ToBitmap(prediction.image)

                        newBitmap?.let {
                            Storage.saveFileToStorage(it, this, file.absolutePath)
                            val diagnosis = Diagnosis(prediction.prediction, file.absolutePath)
                            Storage.saveDiagnosis(this, diagnosis, user.email)
                            imageCount++
                        }
                    }
                }

                Toast.makeText(this, "Synced $imageCount new images", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Sync failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

    }
    

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}