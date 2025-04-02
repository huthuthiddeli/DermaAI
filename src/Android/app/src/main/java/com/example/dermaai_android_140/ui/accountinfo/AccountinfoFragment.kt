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
import com.example.dermaai_android_140.myClasses.HealthCheckResponse
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.ui.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID
import kotlin.jvm.java

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

        viewModel.isHealthy.observe(viewLifecycleOwner) { healthStatus ->
            Toast.makeText(context, healthStatus, Toast.LENGTH_LONG).show()
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
        
        checkHealth(viewModel)



        if (isLogin) {

            val switchToRegisterBtn = view.findViewById<Button>(R.id.switchToRegisterBtn)
            val loginBtn = view.findViewById<Button>(R.id.loginBtn)
            val loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

            setUpObserverLogin(loginViewModel,viewModel)

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

                //
                //hardcoded(viewModel)
                //
                
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                val url = getString(R.string.main) + getString(R.string.user_controller_gateway) + getString(R.string.validateUser_gateway)

                viewModel.login(email,password,false,url)

                
            }

        } else {


            setUpInputValidation(view)
            
            val switchToLoginBtn = view.findViewById<Button>(R.id.switchToLoginBtn)

            setUpObserverRegister(viewModel)


            switchToLoginBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder_accountinfo, newInstance(true))
                    .commit()
            }

            val registerBtn = view.findViewById<Button>(R.id.registerBtn)

            registerBtn.setOnClickListener {

                /*
                val email = view.findViewById<EditText>(R.id.editTextEmail)
                val password = view.findViewById<EditText>(R.id.editTextPassword)
                val confirmPassword = view.findViewById<EditText>(R.id.editTextConfirmPassword)
                 */

                val emailStr = view.findViewById<EditText>(R.id.editTextEmail).text.toString()
                val passwordStr = view.findViewById<EditText>(R.id.editTextPassword).text.toString()
                //val confirmPasswordStr = view.findViewById<EditText>(R.id.editTextConfirmPassword).text.toString()




                val url = getString(R.string.main) + getString(R.string.user_controller_gateway) + getString(R.string.saveUser_gateway)

                viewModel.register(emailStr, passwordStr, url)


            }

        }

        return view
    }

    private fun checkHealth(viewModel: AccountinfoViewModel)
    {
        val url = getString(R.string.main) + getString(R.string.checkHealth_gateway)
        val model = HealthCheckResponse("")

        viewModel.startHealthCheck(model,url)

    }

    private fun setUpInputValidation(view: View): Boolean {
        val email = view.findViewById<EditText>(R.id.editTextEmail)
        val password = view.findViewById<EditText>(R.id.editTextPassword)
        val confirmPassword = view.findViewById<EditText>(R.id.editTextConfirmPassword)

        val emailStr = email.text.toString().trim()
        val passwordStr = password.text.toString()
        val confirmPasswordStr = confirmPassword.text.toString()

        if (emailStr.isEmpty()) {
            email.error = "Email is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.error = "Enter a valid email address"
            return false
        }
        if (passwordStr.isEmpty()) {
            password.error = "Password is required"
            return false
        }
        if (confirmPasswordStr.isEmpty()) {
            confirmPassword.error = "Please confirm your password"
            return false
        }
        if (passwordStr != confirmPasswordStr) {
            confirmPassword.error = "Passwords do not match"
            return false
        }
        if(passwordStr.length < 8)
        {
            password.error = "Password must be at least 8 Characters long"
            return false
        }

        return true
    }

    private fun setUpObserverRegister(viewModel : AccountinfoViewModel)
    {
        viewModel.registerCount.observe(viewLifecycleOwner) { registerCount ->
            if(registerCount > 0)
            {
                Toast.makeText(context, "Successfully registered!", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun setUpObserverLogin(loginViewModel : LoginViewModel, viewModel : AccountinfoViewModel)
    {
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn && viewModel.getUser() != null) {
                Toast.makeText(context, "Credentials are correct!", Toast.LENGTH_LONG).show()

                if (loginViewModel.getStayLoggedIn() == true) {
                    val token = generateToken()
                    val expirationTime = System.currentTimeMillis() + (60 * 60 * 1000 * 24 * 7)

                    storeToken(token, expirationTime)
                }

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())

            } else if (!isLoggedIn && viewModel.getUser() != null) {
                Toast.makeText(context, "Credentials are incorrect!", Toast.LENGTH_LONG).show()
            }
        }


        viewModel.mfaEnabled.observe(viewLifecycleOwner) { mfaEnabled ->
            if (mfaEnabled) {
                signInFirebase()
                showTwoFAInputDialog(requireContext(), viewModel)
            }
        }



    }

    private fun signInFirebase()
    {
        val auth = FirebaseAuth.getInstance()
        val viewModel = ViewModelProvider(this)[AccountinfoViewModel::class.java]
        viewModel.signInFirebase()

    }

    private fun hardcoded(viewModel: AccountinfoViewModel)
    {
        viewModel.createTestUser()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
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

                    Authentication.verifyTOTP(requireActivity(), viewModel.getKey(), code) { isValid ->
                        if (isValid) {
                            Toast.makeText(context, "Verified Successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
                        } else {
                            Toast.makeText(context, "Verification Failed!", Toast.LENGTH_SHORT).show()
                        }
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
