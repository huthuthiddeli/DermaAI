package com.example.dermaai_android_140.myClasses

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Storage {


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

        Storage().addMetadata(tempFile)

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


    fun convertImageToBase64(inputStream: InputStream?, imageUri: Uri?): String? {
        try {
            // Open an input stream from the URI
            val inputStream: InputStream? = inputStream
            inputStream?.use { stream ->
                // Read the image into a ByteArrayOutputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (stream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                // Convert the byte array to a Base64 string
                val imageBytes = byteArrayOutputStream.toByteArray()

                val base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                return base64
            }
        } catch (e: Exception) {
            Log.e("Error", "converting image to Base64 failed", e)
            return null
        }

        return null
    }


}