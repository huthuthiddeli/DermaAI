package com.example.dermaai_android_140.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result



}
