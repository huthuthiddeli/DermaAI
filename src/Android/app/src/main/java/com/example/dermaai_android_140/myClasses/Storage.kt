package com.example.dermaai_android_140.myClasses

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
import java.util.Locale

class Storage {



    fun retrieveImagesFromStorage(filesDir : File?, takenByUser: Boolean): MutableList<File> {

        
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

    fun saveFileToStorage(bitmap : Bitmap, context : Context, filePath : String)
    {
        val outputStream: OutputStream = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        
        Toast.makeText(context, "Image successfully stored!", Toast.LENGTH_SHORT).show()
    }

    fun createUniqueImagePath(activity : Activity, takenByUser : Boolean): File {
        //create File name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        var subDir = getSubDir(takenByUser)

        val dir =  File(storageDir, subDir)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            dir
        )
    }


    private fun getSubDir(takenByUser : Boolean) : String
    {
        if(takenByUser)
        {
            return "Photo_User"
        }
        else {
            return "Photo_ServerResponse"
        }
    }

}