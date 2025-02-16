package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.LoginRepo


class LoginRepoImpl : LoginRepo {


    override fun login(email : String, password : String, mfa : Boolean, key : String) : User?
    {
        val user = User(email,password, mfa)


        //API.callApi("http://93.111.12.119:3333/","","Post",user)

        //TODO
        // send if 2FA is enabled/disabled
        // store 2FA key on Server

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
        return User("a","a", false)
    }

    override fun getUser(): User? {
        return User("a","a", false)
    }
}