package com.example.dermaai_android_140.ui.accountinfo

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.camera.video.internal.encoder.TimeProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.ui.login.LoginViewModel
import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorKey
import dev.samstevens.totp.code.CodeGenerator
import dev.samstevens.totp.code.CodeVerifier
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.code.HashingAlgorithm
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.qr.QrGenerator
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.*
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider
import org.koin.androidx.viewmodel.ext.android.viewModel

//import com.warrenstrange.googleauth.GoogleAuthenticator
//import com.warrenstrange.googleauth.GoogleAuthenticatorKey
import java.net.Authenticator
import java.util.UUID

class AccountinfoFragment() : Fragment() {

    private var isLogin: Boolean = false

    //val viewModel: AccountinfoViewModel by viewModel()

    companion object {
        fun newInstance(isLogin: Boolean): AccountinfoFragment {
            val fragment = AccountinfoFragment()
            fragment.isLogin = isLogin
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View? = null

        //deleteToken()

        val viewModel = ViewModelProvider(this).get(AccountinfoViewModel::class.java)

        if (isLogin) {
            view = inflater.inflate(R.layout.fragment_accountinfo_login, container, false)
        } else {
            view = inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        }



        if (isLogin) {

            val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
            val loginBtn = view.findViewById<Button>(R.id.loginBtn)
            val loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

            viewModel.isLoggedIn.observe(viewLifecycleOwner, { isLoggedIn ->
                if (isLoggedIn) {

                    if(loginViewModel.getStayLoggedIn() == true)
                    {
                        val token = generateToken()
                        val expirationTime = System.currentTimeMillis() + (60 * 60 * 1000 * 24 * 7)

                        storeToken(token,expirationTime)
                    }

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "User is not registered", Toast.LENGTH_LONG).show()
                }
            })


            if(isTokenValid())
            {
                viewModel.setIsLoggedIn(true)
                //loginViewModel.setStayLoggedIn(true)
            }

            //val stayLoggedInContainer = view.findViewById<View>(R.id.stayLoggedInContainer)

            switchToRegisterBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment.newInstance(false))
                    .commit()
            }



            val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
            val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)

            /*
            editTextEmail.setOnClickListener {input ->
                viewModel.setEmail(input.toString())
            }

            editTextPassword.setOnClickListener {input ->
                viewModel.setPassword(input.toString())
            }*/


            loginBtn.setOnClickListener {

                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()



                //Test-Call

                viewModel.loginTest()

                //


                viewModel.login(email, password)

            }



            viewModel.mfaEnabled.observe(viewLifecycleOwner, { mfaEnabled ->
                if (mfaEnabled) {
                    showTwoFAInputDialog(requireContext(),viewModel)
                }
            })


        } else {

            val switchToLoginBtn = view.findViewById<Button>(R.id.switchToLoginBtn)

            switchToLoginBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, AccountinfoFragment.newInstance(true))
                    .commit()
            }
        }

        return view
    }


    private fun showTwoFAInputDialog(context: Context, viewModel : AccountinfoViewModel) {

        val authentication = Authentication()
        val input = EditText(context).apply {
            hint = "Enter your 2FA key"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        AlertDialog.Builder(context)
            .setTitle("Enter 2FA Code")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val code = input.text.toString()
                if (code.isNotEmpty()) {
                    if (authentication.validateTOTP(viewModel.getKey(), code)) {
                        Toast.makeText(context, "Verified Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Invalid Code", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    fun isTokenValid(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("Key", Context.MODE_PRIVATE)
        val expirationTime = sharedPreferences.getLong("tokenExpiration", 0)

        if(System.currentTimeMillis() < expirationTime)
        {
            return true
        }
        else{
            return false
        }
    }

    fun storeToken(token: String, expirationTime: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("Key", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Key", token)
        editor.putLong("tokenExpiration", expirationTime)
        editor.apply()
    }

    private fun generateToken(): String {
        return UUID.randomUUID().toString()
    }

    fun deleteToken() {
        val sharedPreferences = requireContext().getSharedPreferences("Key", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("Key")
        editor.remove("tokenExpiration")
        editor.apply()
    }

}
