package com.example.dermaai_android_140.ui.settings

import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Storage
import java.io.File
import com.example.dermaai_android_140.myClasses.Diagnosis

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

        //  back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        settingsViewModel.allPredictions.observe(this) { response ->
            val filesDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            var imageCount = 0
            val localImages = Storage.retrieveImagesFromStorage(filesDir, settingsViewModel.getCurrentUser()!!.email)


            response!!.predictions.forEach { prediction ->
                for (localImage in localImages) {


                    val localBase64 = Storage.convertImageToBase64(localImage)
                    if(!localBase64.equals(prediction.image))
                    {
                        val file = Storage.createUniqueImagePath(this, settingsViewModel.getCurrentUser()!!.email)
                        val newBitmap = Storage.base64ToBitmap(prediction.image)
                        if (newBitmap != null) {
                            Storage.saveFileToStorage(newBitmap, baseContext, file.absolutePath)
                            val diagnosis = Diagnosis(prediction.prediction,file.absolutePath)
                            Storage.saveDiagnosis(this,diagnosis, settingsViewModel.getCurrentUser()!!.email)
                            imageCount++
                        }

                    }

                }
            }
            //Toast.makeText(context, "Synchronized $imageCount images", Toast.LENGTH_LONG).show()
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