package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.repo.LoginRepo
import com.example.dermaai_android_140.myClasses.User


class LoginRepoImpl : LoginRepo {

    override fun login(email : String, password : String) : User?
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

    override fun register() : User?
    {
        return User("a","a")
    }
}