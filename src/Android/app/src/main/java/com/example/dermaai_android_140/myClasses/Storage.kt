package com.example.dermaai_android_140.myClasses

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.ExifInterface
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
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
import java.io.ByteArrayOutputStream



class Storage {

    companion object{

        fun base64ToBitmap(base64String: String): Bitmap? {
            return try {
                val decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                Log.e("Storage", "Error converting Base64 to Bitmap: ${e.message}")
                null
            }
        }


        fun retrieveImagesFromStorage(filesDir: File?, username : String): MutableList<File> {

            var subDir = getSubDir(username)
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



        // Save diagnosis to file (all in one file)
        fun saveDiagnosis(activity: Activity, diagnosis: Diagnosis, username : String) {
            try {
                // 1. Create or find storage folder
                var subDir = "Diagnoses_" + username

                val folder = File(activity.getExternalFilesDir(null), subDir)
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
        fun readAllDiagnoses(activity: Activity, username : String): List<Diagnosis> {
            return try {
                val subdir = "Diagnoses_" + username + "/all_diagnoses.json"
                val file = File(activity.getExternalFilesDir(null), subdir)
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
        fun readDiagnosisForImage(activity: Activity, imagePath: String, username : String): Map<String, Double>? {
            val allDiagnoses = readAllDiagnoses(activity, username)
            return allDiagnoses.find { it.imagePath == imagePath }?.prediction
        }


        fun createReportFile(activity: Activity, report : String, username : String) {

            try {

                //create File name
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                var subDir = "Reports_" + username

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
        

        fun createUniqueImagePath(activity: Activity, username : String): File {
            //create File name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            var subDir = getSubDir(username)

            val dir = File(storageDir, subDir)

            if (!dir.exists()) {
                dir.mkdirs()
            }


            val tempFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                dir
            )
            //Storage.addMetadata(tempFile)

            return tempFile
        }




        private fun getSubDir(username : String): String {
            return "Photo_User_" + username
        }

        fun convertImageToBase64(imageFile: File): String? {
            return try {

                val compressQuality: Int = 100

                // 2. Decode the image file into a Bitmap
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565 // More memory efficient
                }
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                    ?: throw IOException("Failed to decode image file")

                // 3. Compress the bitmap into a ByteArray
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                // 4. Convert to Base64 and return
                android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e("Storage", "Error converting image to Base64: ${e.message}")
                null
            }
        }



    }
}



