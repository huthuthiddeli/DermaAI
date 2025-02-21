package com.example.dermaai_android_140.myClasses

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class API {

    companion object {

        fun callApi(apiUrl: String, token: String, httpMethod: String, requestModel: Any? = null): Result<String> {
            return try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = httpMethod
                
                //setRequestHeaders(connection, token)
                sendRequest(connection, httpMethod, requestModel)

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    //val test = readResponse(connection.inputStream)
                    return Result.success(readResponse(connection.inputStream))
                } else {
                    val errorResponse = readResponse(connection.errorStream)
                    println("Error Response Code: $responseCode, Message: ${connection.responseMessage}, Body: $errorResponse")
                    return Result.failure(Exception("HTTP error code: $responseCode"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return Result.failure(e)
            }
        }

        private fun setRequestHeaders(connection: HttpURLConnection, token: String) {
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $token")
        }

        private fun sendRequest(connection: HttpURLConnection, httpMethod: String, requestModel: Any?) {

            // GET is handled automatically


            // Code for POST
            if (httpMethod == "POST" || httpMethod == "PUT") {
                sendPost(connection, requestModel)
            }
        }

        private fun sendPost(connection: HttpURLConnection, requestModel: Any?)
        {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

            requestModel?.let {
                val jsonInput = Gson().toJson(it)


                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { os ->
                    os.write(jsonInput)
                    os.flush()
                }
            }
        }
        

        private fun readResponse(inputStream: java.io.InputStream): String {
            val reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line?.trim())
            }
            reader.close()
            return response.toString()
        }
    }
}