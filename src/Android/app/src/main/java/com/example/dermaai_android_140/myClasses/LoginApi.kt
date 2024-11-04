package com.example.dermaai_android_140.myClasses

import com.google.gson.Gson
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class LoginApi : Api() {


    fun login(email : String, password : String) : User? //= withContext(Dispatchers.IO)
    {
        val user : User = User(email,password)


        /*
        val url : String = ""
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")


        return try {

            val json = Gson().toJson(user)


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
        return user
    }

    fun register()
    {

    }



}