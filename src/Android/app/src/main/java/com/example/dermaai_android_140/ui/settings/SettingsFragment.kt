package com.example.dermaai_android_140.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.ui.login.LoginActivity

class SettingsFragment : PreferenceFragmentCompat() {


    //private var settingsViewModel: SettingsViewModel by viewModel()
    private lateinit var  settingsViewModel : SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        settingsViewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enable2faSwitch: SwitchPreferenceCompat? = findPreference("enable_2fa")
        val logoutBtn: Preference? = findPreference("logout")
        val syncBtn: Preference? = findPreference("sync_images")

        settingsViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }




        settingsViewModel.currentUser.observe(viewLifecycleOwner){user ->

            if (enable2faSwitch != null && user != null) {
                enable2faSwitch.isEnabled = user.mfa
            }
        }


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

        syncBtn?.setOnPreferenceClickListener {
            Toast.makeText(context, "Syncing Images...", Toast.LENGTH_SHORT).show()
            syncWithDb(settingsViewModel)
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }

    private fun syncWithDb(settingsViewModel : SettingsViewModel)
    {
        val url = getString(R.string.main) +
                getString(R.string.user_controller_gateway) +
                getString(R.string.loadPrediction_gateway)

        settingsViewModel.syncImages(url)
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