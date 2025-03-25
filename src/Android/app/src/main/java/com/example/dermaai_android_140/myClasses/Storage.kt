package com.example.dermaai_android_140.myClasses

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.gson.reflect.TypeToken

class Storage {

    companion object{

        fun retrieveImagesFromStorage(filesDir: File?, takenByUser: Boolean): MutableList<File> {

            var subDir = getSubDir(takenByUser)

            val folder = File(filesDir, subDir)
            val images = mutableListOf<File>()

            if (folder.isDirectory) {
                for (file in folder.listFiles()!!) {
                    if (file.extension == "jpg") {
                        images.add(file)
                    }
                }
            }
            return images
        }

        fun saveFileToStorage(bitmap: Bitmap, context: Context, filePath: String) {
            val outputStream: OutputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Toast.makeText(context, "Image successfully stored!", Toast.LENGTH_SHORT).show()
        }




        fun savePredictionToImageMetadata(imagePath: String, prediction: Map<String, Int>) {
            try {
                val exif = ExifInterface(imagePath)

                // Convert the prediction map to a JSON string
                val predictionJson = prediction.entries.joinToString(",") { "${it.key}:${it.value}" }

                // Save the prediction in the metadata
                exif.setAttribute("Prediction", predictionJson)
                exif.saveAttributes() // Save changes
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        fun readPredictionFromImageMetadata(imagePath: String): String? {

            var predictionStr : String? = null

            try {
                val exif = ExifInterface(imagePath)

                // Retrieve the prediction JSON string from the metadata
                predictionStr = exif.getAttribute("Prediction")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return predictionStr
        }


        // Save diagnosis to file (all in one file)
        fun saveDiagnosis(activity: Activity, imagePath: String, diagnosis: Diagnosis) {
            try {
                // 1. Create or find storage folder
                val folder = File(activity.getExternalFilesDir(null), "diagnoses")
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                // 2. Create the diagnosis file
                val file = File(folder, "all_diagnoses.json")

                // 3. Read old diagnoses if file exists
                val oldDiagnoses = if (file.exists()) {
                    val jsonString = file.readText()
                    Gson().fromJson(jsonString, Array<Diagnosis>::class.java).toList()
                } else {
                    emptyList()
                }

                // 4. Add new diagnosis
                val newDiagnosis = diagnosis
                val allDiagnoses = oldDiagnoses + newDiagnosis

                // 5. Save back to file
                file.writeText(Gson().toJson(allDiagnoses))

            } catch (e: Exception) {
                println("Error saving diagnosis: ${e.message}")
            }
        }

        // Read all diagnoses from file
        fun readAllDiagnoses(activity: Activity): List<Diagnosis> {
            return try {
                val file = File(activity.getExternalFilesDir(null), "diagnoses/all_diagnoses.json")
                if (file.exists()) {
                    val jsonString = file.readText()
                    Gson().fromJson(jsonString, Array<Diagnosis>::class.java).toList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                println("Error reading diagnoses: ${e.message}")
                emptyList()
            }
        }

        // Read diagnosis for a specific image
        fun readDiagnosisForImage(activity: Activity, imagePath: String): Map<String, Int>? {
            val allDiagnoses = readAllDiagnoses(activity)
            return allDiagnoses.find { it.imagePath == imagePath }?.prediction
        }


        fun createReportFile(activity: Activity, report : String) {

            try {

                //create File name
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                var subDir = "Reports"

                val dir = File(storageDir, subDir)

                if (!dir.exists()) {
                    dir.mkdirs()
                }


                val tempFile = File.createTempFile(
                    "HTML_${timeStamp}_",
                    ".html",
                    dir
                )



                tempFile.writeText(report)

            } catch (e: Exception){
            e.printStackTrace()
            }

        }



        fun createUniqueImagePath(activity: Activity, takenByUser: Boolean): File {
            //create File name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            var subDir = getSubDir(takenByUser)

            val dir = File(storageDir, subDir)

            if (!dir.exists()) {
                dir.mkdirs()
            }


            val tempFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                dir
            )
            Storage.addMetadata(tempFile)

            return tempFile
        }


        private fun removeMetadata() {

        }

        fun addMetadata(image: File) {
            try {
                val exif = ExifInterface(image)
                exif.setAttribute(ExifInterface.TAG_USER_COMMENT, "Text")
                exif.saveAttributes()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        private fun getSubDir(takenByUser: Boolean): String {
            return if (takenByUser) {
                "Photo_User"
            } else {
                "Photo_ServerResponse"
            }
        }
    }


}