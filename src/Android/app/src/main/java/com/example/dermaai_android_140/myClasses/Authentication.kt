package com.example.dermaai_android_140.myClasses

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import android.util.Base64
import android.util.Log
import dev.samstevens.totp.code.CodeGenerator
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.firestore.SetOptions



class Authentication {

    companion object {
        private const val REQUEST_SIGN_IN = 1001
        private lateinit var KEY_ALIAS : String

        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        /**
         * Starts sign-in via browser using Firebase Authentication.
         * Pass the Activity instance to launch the sign-in intent.
         */
        fun signInWithBrowser(activity: Activity, alias : String) {

            KEY_ALIAS = alias

            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),  // Google Sign-In
            )
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            activity.startActivityForResult(signInIntent, REQUEST_SIGN_IN)
        }

        /**
         * Handles the sign-in result.
         */
        fun handleSignInResult(
            activity: Activity,
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) {
            if (requestCode == REQUEST_SIGN_IN) {
                val response = IdpResponse.fromResultIntent(data)
                if (resultCode == Activity.RESULT_OK) {
                    val user = auth.currentUser
                    Log.d("Auth", "User signed in: ${user?.email}")
                } else {
                    Log.e("Auth", "Sign-in failed: ${response?.error?.errorCode}")
                }
            }
        }

        /**
         * Generates and saves a TOTP secret for 2FA.
         * Saves the secret both to Firestore and the Keystore.
         */
        fun enable2FA(activity: Activity, userId: String, callback: (String) -> Unit) {
            val secret = generateSecret(activity)
            val user2FAData = hashMapOf(
                "2fa_enabled" to true,
                "secret_key" to secret
            )
            db.collection("users").document(userId)
                .set(user2FAData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Firestore", "2FA key saved successfully")
                    saveSecretToKeystore(activity, secret)
                    callback(secret)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error saving 2FA key: ${e.message}")
                }
        }

        /**
         * Verifies a TOTP code entered by the user.
         * It first checks the Keystore, and if the secret isn't found, it falls back to Firebase.
         */
        fun verifyTOTP(
            activity: Activity,
            userId: String,
            userInputCode: String,
            callback: (Boolean) -> Unit
        ) {
            // First check in Keystore
            val secretKey = getSecretFromKeystore(activity)
            if (secretKey != null) {
                val isValid = validateTOTP(secretKey, userInputCode)
                callback(isValid)
            } else {
                // Retrieve from Firebase if not found locally
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val firebaseSecretKey = document.getString("secret_key")
                            if (firebaseSecretKey != null) {
                                saveSecretToKeystore(activity, firebaseSecretKey)
                                val isValid = validateTOTP(firebaseSecretKey, userInputCode)
                                callback(isValid)
                            } else {
                                callback(false)
                            }
                        } else {
                            callback(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error verifying 2FA: ${e.message}")
                        callback(false)
                    }
            }
        }

        /**
         * Disables 2FA by removing the TOTP secret from Firestore and Keystore.
         */
        fun disable2FA(activity: Activity, userId: String, callback: (Boolean) -> Unit) {
            db.collection("users").document(userId)
                .update("2fa_enabled", false, "secret_key", null)
                .addOnSuccessListener {
                    Log.d("Firestore", "2FA disabled successfully")
                    removeSecretFromKeystore()
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error disabling 2FA: ${e.message}")
                    callback(false)
                }
        }

        /**
         * Retrieves the 2FA secret key from Firebase if not already present in the Keystore.
         */
        fun get2FAKey(activity: Activity, userId: String, callback: (String?) -> Unit) {
            val secretKey = getSecretFromKeystore(activity)
            if (secretKey != null) {
                callback(secretKey)
            } else {
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val secret = document.getString("secret_key")
                            callback(secret)
                        } else {
                            callback(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error getting 2FA key: ${e.message}")
                        callback(null)
                    }
            }
        }

        /**
         * Generates a TOTP secret key.
         */
        private fun generateSecret(context: Context): String {
            val secretGenerator: SecretGenerator = DefaultSecretGenerator()
            val secret = secretGenerator.generate()
            saveHash(secret, context)
            return secret
        }

        /**
         * Validates a TOTP code using the provided secret.
         */
        private fun validateTOTP(secret: String, code: String): Boolean {
            val timeProvider = SystemTimeProvider()
            val codeGenerator: CodeGenerator = DefaultCodeGenerator()
            val verifier = DefaultCodeVerifier(codeGenerator, timeProvider)
            return verifier.isValidCode(secret, code)
        }


        /**
         * Saves the secret securely in the Keystore via EncryptedSharedPreferences.
         */
        private fun saveSecretToKeystore(activity: Activity, secret: String) {
            val keystore = KeyStore.getInstance("AndroidKeyStore")
            keystore.load(null)
            // Create key if it doesn't exist
            if (!keystore.containsAlias(KEY_ALIAS)) {
                val keyGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
            // Encrypt the secret
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeyFromKeystore())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(secret.toByteArray())
            // Save the encrypted secret and IV using EncryptedSharedPreferences
            val sharedPrefs = EncryptedSharedPreferences.create(
                "keystore_prefs",
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                activity,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sharedPrefs.edit {
                putString("2fa_secret_key", encryptedBytes.joinToString(",") { it.toString() })
                putString("iv", iv.joinToString(",") { it.toString() })
            }
        }

        /**
         * Retrieves the secret from the Keystore (via EncryptedSharedPreferences), decrypting it.
         */
        private fun getSecretFromKeystore(activity: Activity): String? {
            val sharedPrefs = EncryptedSharedPreferences.create(
                "keystore_prefs",
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                activity,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val encryptedSecretString = sharedPrefs.getString("2fa_secret_key", null)
            val ivString = sharedPrefs.getString("iv", null)
            if (encryptedSecretString != null && ivString != null) {
                val encryptedBytes =
                    encryptedSecretString.split(",").map { it.toByte() }.toByteArray()
                val iv = ivString.split(",").map { it.toByte() }.toByteArray()
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKeyFromKeystore(),
                    GCMParameterSpec(128, iv)
                )
                val decryptedBytes = cipher.doFinal(encryptedBytes)
                return String(decryptedBytes)
            }
            return null
        }


        /**
         * Retrieves the secret key from the Android Keystore.
         */
        private fun getSecretKeyFromKeystore(): SecretKey {
            val keystore = KeyStore.getInstance("AndroidKeyStore")
            keystore.load(null)
            return keystore.getKey(KEY_ALIAS, null) as SecretKey
        }

        /**
         * Removes the secret from the Keystore.
         */
        private fun removeSecretFromKeystore() {
            val keystore = KeyStore.getInstance("AndroidKeyStore")
            keystore.load(null)
            keystore.deleteEntry(KEY_ALIAS)
        }

        /**
         * Saves the secret in a secure location.
         * (This is a placeholder function for additional secure storage if needed.)
         */
        private fun saveHash(secret: String, context: Context) {
            Log.d("SecureStorage", "Saving secret securely (not implemented)")
        }

    }
}





    /*
    companion object {

        fun generateSecret(context: Context) : String
        {
            val secretGenerator : SecretGenerator = DefaultSecretGenerator()
            val secret = secretGenerator.generate()
            saveHash(secret, context)
            return secret
        }

        fun validateTOTP(secret : String, code : String) : Boolean
        {
            val timeProvider = SystemTimeProvider()
            val codeGenerator: CodeGenerator = DefaultCodeGenerator()
            val verifier = DefaultCodeVerifier(codeGenerator, timeProvider)
            return verifier.isValidCode(secret, code)
        }


        fun saveHash(hash : String, context : Context){

            saveEncryptedHashInKeystore(context, "2FA_Key",hash)

        }

        fun saveEncryptedHashInKeystore(context: Context, keyAlias: String, hash: String) {

            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            // 1. Generate or retrieve a secret key (AES)
            val secretKey: SecretKey = if (!keyStore.containsAlias(keyAlias)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(false) // true: biometric or PIN protection
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            } else {
                getSecretKeyFromKeystore(keyAlias) as SecretKey
            }


            // 2. Create a Cipher instance for encryption
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv

            // Encrypt the hash
            val encryptedHash = cipher.doFinal(hash.toByteArray())

            // 3. Save the IV and encrypted hash in SharedPreferences
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit() {

                // Convert IV and encrypted hash to Base64 strings
                val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
                val encryptedHashBase64 = Base64.encodeToString(encryptedHash, Base64.DEFAULT)

                // Save to SharedPreferences
                putString("${keyAlias}_iv", ivBase64)
                putString("${keyAlias}_hash", encryptedHashBase64)
            }

            //val oriKey = retrieveEncryptedHashFromKeystore(context, "2FA_Key")

        }


        private fun getSecretKeyFromKeystore(keyAlias: String): SecretKey? {
            return try {
                val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                return keyStore.getKey(keyAlias, null) as SecretKey
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }


        fun retrieveEncryptedHashFromKeystore(context: Context, keyAlias: String): String? {
            try {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)

                // Retrieve the secret key
                val secretKey = getSecretKeyFromKeystore(keyAlias)

                // Retrieve the IV and encrypted hash from SharedPreferences
                val sharedPreferences: SharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
                val ivBase64 = sharedPreferences.getString("${keyAlias}_iv", null) ?: return null
                val encryptedHashBase64 = sharedPreferences.getString("${keyAlias}_hash", null) ?: return null

                val iv = Base64.decode(ivBase64, Base64.DEFAULT)
                val encryptedHash = Base64.decode(encryptedHashBase64, Base64.DEFAULT)

                // 4. Create a Cipher instance for decryption
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val spec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

                // Decrypt the hash
                val decryptedHash = cipher.doFinal(encryptedHash)

                return String(decryptedHash)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }







        



    }
    */



