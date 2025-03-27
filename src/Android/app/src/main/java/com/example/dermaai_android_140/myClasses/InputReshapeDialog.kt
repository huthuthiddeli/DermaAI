package com.example.dermaai_android_140.myClasses


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.example.dermaai_android_140.R
import com.google.android.material.textfield.TextInputLayout

class InputReshapeDialog(
    context: Context,
    private val onReshapeSubmitted: (reshapeSize: Int) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_input_reshape) // You'll need to create this layout

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val tilReshape = findViewById<TextInputLayout>(R.id.til_reshape)
        val etReshape = findViewById<EditText>(R.id.et_reshape)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        btnSubmit.setOnClickListener {
            val reshape = etReshape.text.toString().toIntOrNull()

            if (reshape == null) {
                tilReshape.error = "Please enter valid number"
            } else {
                onReshapeSubmitted(reshape)
                dismiss()
            }
        }
    }
}