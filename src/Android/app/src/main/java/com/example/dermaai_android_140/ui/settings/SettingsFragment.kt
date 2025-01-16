package com.example.dermaai_android_140.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.dermaai_android_140.R
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.dermaai_android_140.myClasses.Authentication
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.preference.EditTextPreference
import android.content.Intent
import com.example.dermaai_android_140.ui.login.LoginActivity

class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val enable2faSwitch: SwitchPreferenceCompat? = findPreference("enable_2fa")
        val logoutBtn: Preference? = findPreference("logout")

        enable2faSwitch?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val is2faEnabled = newValue as Boolean
            if (is2faEnabled) {
                enable2FA()
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
        sharedPreferences.edit().clear().apply()

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    fun enable2FA() {

        val authentication = Authentication()
        val key = authentication.generateSecret(requireContext())
        val FaKeySwitch: SwitchPreferenceCompat? = findPreference("enable_2fa")
        val FaKeyInput: EditTextPreference? = findPreference("two_fa_key")

        saveToClipboard(key)

        FaKeyInput?.isEnabled = true

        FaKeyInput?.setOnPreferenceChangeListener { preference, enteredCode ->

            val correctCode = authentication.validateTOTP(key,enteredCode.toString())

            if(correctCode)
            {
                Toast.makeText(context, "Correct Code: 2FA Activated", Toast.LENGTH_SHORT).show()
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