package com.example.dermaai_android_140.myClasses

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import com.example.dermaai_android_140.myClasses.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginApi : Api() {

    
    fun login2(username: String, token: String) {
        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            loginRepository.makeLoginRequest(jsonBody)
        }
    }

    suspend fun login(email : String, password : String) : User? = withContext(Dispatchers.IO)
    {
        val user : User = User(email,password)

        println()




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