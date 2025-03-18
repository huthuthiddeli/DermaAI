package com.example.dermaai_android_140.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoViewModel
import com.example.dermaai_android_140.ui.gallery.GalleryViewModel
import com.example.dermaai_android_140.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.koin.java.KoinJavaComponent
import kotlin.getValue

class SettingsFragment : PreferenceFragmentCompat() {

    private val loginRepo: LoginRepoImpl by KoinJavaComponent.inject(LoginRepoImpl::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]


        val enable2faSwitch: SwitchPreferenceCompat? = findPreference("enable_2fa")
        val logoutBtn: Preference? = findPreference("logout")

        enable2faSwitch?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val is2faEnabled = newValue as Boolean
            if (is2faEnabled) {
                enable2FA(settingsViewModel)
            } else {
                disable2FA()
            }
            true
        }


        logoutBtn?.setOnPreferenceClickListener {
            logout()
            true
        }



    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("Key", Context.MODE_PRIVATE)
        sharedPreferences.edit() { clear() }

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    fun enable2FA(settingsViewModel : SettingsViewModel) {

        val key = settingsViewModel.generate2faKey(requireContext())

        val FaKeySwitch: SwitchPreferenceCompat? = findPreference("enable_2fa")
        val FaKeyInput: EditTextPreference? = findPreference("two_fa_key")

        saveToClipboard(key)


        FaKeyInput?.isEnabled = true
        
        FaKeyInput?.setOnPreferenceChangeListener { preference, enteredCode ->

            val correctCode = settingsViewModel.validate2faCode(key,enteredCode.toString())
            
            if(correctCode)
            {
                Toast.makeText(context, "Correct Code: 2FA Activated", Toast.LENGTH_SHORT).show()
                //

                val url = getString(R.string.main) + getString(R.string.user_controller_gateway) + getString(R.string.setMfa)

                val viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
                
                val receivedUser = viewModel.setMfa(url)


                if(receivedUser != null)
                {
                    Toast.makeText(context, "Mfa set!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "Failure!", Toast.LENGTH_SHORT).show()
                }

                true
            }
            else
            {
                Toast.makeText(context, "Incorrect Code", Toast.LENGTH_SHORT).show()
                FaKeySwitch?.isEnabled = false
                false
            }
        }
    }


    fun saveToClipboard(key : String)
    {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("2FA Key copied to Clipboard", key)
        clipboard.setPrimaryClip(clip)
    }


    fun disable2FA() {

        val twoFAKeyInput = findPreference<EditTextPreference>("two_fa_key")
        twoFAKeyInput?.isEnabled = false



        //FaKeyInput?.setText("")
    }

}