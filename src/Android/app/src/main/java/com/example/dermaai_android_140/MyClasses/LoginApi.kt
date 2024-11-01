package com.example.dermaai_android_140.MyClasses

import android.widget.Toast
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.Charsets.UTF_8

class LoginApi : Api() {

    fun login(email : String, password : String) : User?
    {
        val user = User(email,password)
/*
        val url : String = ""

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")

        return try {

            val gson = Gson()
            //val json : Json = Json.encodeToJsonElement(user)
            val json = gson.toJson(user)


            OutputStreamWriter(connection.outputStream, UTF_8).use { os ->
                os.write(json)
                os.flush()
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {

                connection.inputStream.bufferedReader().use { reader ->
                    val response = reader.readText()
                    println("Response from server: $response")

                    // return:
                    user
                }
            } else {
                //Toast.makeText(context, connection.responseCode, Toast.LENGTH_LONG).show()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
*/
        return User("T","T")
    }

    fun register()
    {

    }



}