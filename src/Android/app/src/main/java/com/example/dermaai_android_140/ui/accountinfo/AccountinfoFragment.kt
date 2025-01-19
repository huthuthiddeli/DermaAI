package com.example.dermaai_android_140.ui.accountinfo

//import com.warrenstrange.googleauth.GoogleAuthenticator
//import com.warrenstrange.googleauth.GoogleAuthenticatorKey
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.ui.login.LoginViewModel
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

        val viewModel = ViewModelProvider(this)[AccountinfoViewModel::class.java]

        view = if (isLogin) {
            inflater.inflate(R.layout.fragment_accountinfo_login, container, false)
        } else {
            inflater.inflate(R.layout.fragment_accountinfo_register, container, false)
        }


        if (isLogin) {

            val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
            val loginBtn = view.findViewById<Button>(R.id.loginBtn)
            val loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

            viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
                if (isLoggedIn) {

                    if (loginViewModel.getStayLoggedIn() == true) {
                        val token = generateToken()
                        val expirationTime = System.currentTimeMillis() + (60 * 60 * 1000 * 24 * 7)

                        storeToken(token, expirationTime)
                    }

                    val intent = Intent(requireContext(), MainActivity::class.java)

                    startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())

                } else {
                    Toast.makeText(context, "Credentials are incorrect!", Toast.LENGTH_LONG).show()
                }
            }


            if(isTokenValid())
            {
                viewModel.setIsLoggedIn(true)
                //loginViewModel.setStayLoggedIn(true)
            }

            //val stayLoggedInContainer = view.findViewById<View>(R.id.stayLoggedInContainer)

            switchToRegisterBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, newInstance(false))
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

                viewModel.loginTest(getString(R.string.main_login))

                //getString(R.string.main)


                //


                viewModel.login(email, password)

            }



            viewModel.mfaEnabled.observe(viewLifecycleOwner) { mfaEnabled ->
                if (mfaEnabled) {
                    showTwoFAInputDialog(requireContext(), viewModel)
                }
            }


        } else {

            val switchToLoginBtn = view.findViewById<Button>(R.id.switchToLoginBtn)

            switchToLoginBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, newInstance(true))
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

        return System.currentTimeMillis() < expirationTime
    }

    fun storeToken(token: String, expirationTime: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("Key", Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            putString("Key", token)
            putLong("tokenExpiration", expirationTime)
        }
    }

    private fun generateToken(): String {
        return UUID.randomUUID().toString()
    }

    fun deleteToken() {
        val sharedPreferences = requireContext().getSharedPreferences("Key", Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            remove("Key")
            remove("tokenExpiration")
        }
    }

}
