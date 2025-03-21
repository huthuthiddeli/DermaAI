/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.dermaai_android_140.myClasses


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.example.dermaai_android_140.R
import com.google.android.material.textfield.TextInputLayout


class InputEpochReshapeDialog(
    context: Context,
    private val onInputSubmitted: (epochs: Int, reshapeSize: Int) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_input_epoch_reshape)

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val tilEpoch = findViewById<TextInputLayout>(R.id.til_epoch)
        val tilReshape = findViewById<TextInputLayout>(R.id.til_reshape)
        val etEpoch = findViewById<EditText>(R.id.et_epoch)
        val etReshape = findViewById<EditText>(R.id.et_reshape)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        btnSubmit.setOnClickListener {
            val epoch = etEpoch.text.toString().toIntOrNull()
            val reshape = etReshape.text.toString().toIntOrNull()

            when {
                epoch == null -> tilEpoch.error = "Please enter valid number"
                reshape == null -> tilReshape.error = "Please enter valid number"
                else -> {
                    onInputSubmitted(epoch, reshape)
                    dismiss()
                }
            }
        }
    }
}