package com.example.dermaai_android_140.MyClasses

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

class Storage {


    fun retrieveImagesFromStorage(activity : Activity): MutableList<Bitmap> {

        val folder = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val images = mutableListOf<Bitmap>()

        if (folder != null && folder.isDirectory) {
            // !!
            for (file in folder.listFiles()) {

                if (file.extension == "jpg" && folder.listFiles() != null) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null) {
                        images.add(bitmap)
                    }
                }

            }
        }
        return images
    }

    fun saveFileToStorage(bitmap : Bitmap, activity : Activity, context : Context)
    {
        val filePath = createUniqueImagePath(activity)
        val outputStream: OutputStream = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush()
        outputStream.close()
        
        Toast.makeText(context, "Image successfully stored!", Toast.LENGTH_SHORT).show()
    }

    private fun createUniqueImagePath(activity : Activity): File {

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )

    }

}