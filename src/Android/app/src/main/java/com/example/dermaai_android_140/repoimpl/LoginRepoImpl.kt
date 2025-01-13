package com.example.dermaai_android_140.repoimpl

import android.content.Context
import com.example.dermaai_android_140.repo.LoginRepo
import com.example.dermaai_android_140.myClasses.User
import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.text.Charsets.UTF_8


class LoginRepoImpl : LoginRepo {


    override fun login(email : String, password : String, mfa : Boolean, key : String) : User?
    {
        val user : User = User(email,password, mfa,key)
        
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


    fun test(context : Context)
    {

        val myBuilder = CronetEngine.Builder(context)
        val cronetEngine: CronetEngine = myBuilder.build()

        val executor: Executor = Executors.newSingleThreadExecutor()

        /*
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            "http://10.10.1.193:8080/",
            MyUrlRequestCallback(),
            executor
        )

        val request: UrlRequest = requestBuilder.build()*/

    }
    

    override fun register() : User?
    {
        return User("a","a", false, "")
    }

    override fun getUser(): User? {
        return User("a","a", false, "")
    }
}