package com.example.dermaai_android_140.myClasses

import android.annotation.SuppressLint
import android.net.http.HttpException
import android.net.http.UrlRequest
import android.os.Build
import android.util.Log
import androidx.activity.result.launch
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer


private const val TAG = "MyUrlRequestCallback"


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class RequestCallback : UrlRequest.Callback {

    override fun onRedirectReceived(
        request: UrlRequest,
        info: android.net.http.UrlResponseInfo,
        newLocationUrl: String
    ) {

        Log.d(TAG, "Redirect received to: " + newLocationUrl)

        request.followRedirect()
    }

    override fun onResponseStarted(
        request: UrlRequest,
        info: android.net.http.UrlResponseInfo
    ) {
        //Log.d(TAG, "Response started, content length: "+ info.contentLength)

    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onReadCompleted(
        request: UrlRequest,
        info: android.net.http.UrlResponseInfo,
        byteBuffer: ByteBuffer
    ) {
        Log.d(TAG, "Read completed, bytes received: ${byteBuffer.remaining()}")

        scope.launch(Dispatchers.IO) {
            // Process data asynchronously
            
        }
    }

    override fun onSucceeded(
        request: UrlRequest,
        info: android.net.http.UrlResponseInfo
    ) {
        TODO("Not yet implemented")
    }

    override fun onFailed(
        request: UrlRequest,
        info: android.net.http.UrlResponseInfo?,
        error: HttpException
    ) {
        TODO("Not yet implemented")
    }

    override fun onCanceled(
        request: UrlRequest,
        info: android.net.http.UrlResponseInfo?
    ) {
        TODO("Not yet implemented")
    }

}