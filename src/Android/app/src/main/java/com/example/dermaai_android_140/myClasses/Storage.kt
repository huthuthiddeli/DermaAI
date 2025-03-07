package com.example.dermaai_android_140.myClasses

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.os.Environment
import android.widget.Toast
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


        fun saveDiagnosisToFile(imagePath: String, diagnosis: Map<String, Int>) {
            try {
                // Get the directory of the image
                val imageFile = File(imagePath)
                val imageDir = imageFile.parentFile

                // Create the diagnosis file in the same directory
                val diagnosisFile = File(imageDir, "${imageFile.nameWithoutExtension}_diagnosis.json")
                val diagnosisJson = Gson().toJson(diagnosis)
                diagnosisFile.writeText(diagnosisJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        // Read diagnosis from a separate file
        fun readDiagnosisFromFile(imagePath: String): Map<String, Int>? {
            return try {
                // Get the directory of the image
                val imageFile = File(imagePath)
                val imageDir = imageFile.parentFile

                // Locate the diagnosis file in the same directory
                val diagnosisFile = File(imageDir, "${imageFile.nameWithoutExtension}_diagnosis.json")
                if (diagnosisFile.exists()) {
                    val diagnosisJson = diagnosisFile.readText()
                    Gson().fromJson(diagnosisJson, Map::class.java) as Map<String, Int>
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
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