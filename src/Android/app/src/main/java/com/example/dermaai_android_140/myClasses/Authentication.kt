package com.example.dermaai_android_140.myClasses

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class Authentication(private val activity: Activity) {


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val REQUEST_SIGN_IN = 1001
    }

    /**
     * Starts sign-in via browser using Firebase Authentication.
     */
    fun signInWithBrowser() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),  // Google Sign-In
            AuthUI.IdpConfig.EmailBuilder().build()   // Email + Password
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
    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
     */
    fun enable2FA(userId: String, context: Context, callback: (String) -> Unit) {
        val secret = generateSecret(context)

        val user2FAData = hashMapOf(
            "2fa_enabled" to true,
            "secret_key" to secret
        )

        db.collection("users").document(userId)
            .set(user2FAData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "2FA key saved successfully")
                callback(secret)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving 2FA key: ${e.message}")
            }
    }

    /**
     * Verifies a TOTP code entered by the user.
     */
    fun verifyTOTP(userId: String, userInputCode: String, callback: (Boolean) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val secretKey = document.getString("secret_key") ?: return@addOnSuccessListener

                    val isValid = validateTOTP(secretKey, userInputCode)
                    callback(isValid)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error verifying 2FA: ${e.message}")
                callback(false)
            }
    }

    /**
     * Disables 2FA by removing the TOTP secret from Firestore.
     */
    fun disable2FA(userId: String, callback: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .update("2fa_enabled", false, "secret_key", null)
            .addOnSuccessListener {
                Log.d("Firestore", "2FA disabled successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error disabling 2FA: ${e.message}")
                callback(false)
            }
    }

    /**
     * Retrieves the 2FA secret key from Firebase.
     */
    fun get2FAKey(userId: String, callback: (String?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val secretKey = document.getString("secret_key")
                    callback(secretKey)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting 2FA key: ${e.message}")
                callback(null)
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
     * Validates a TOTP code.
     */
    private fun validateTOTP(secret: String, code: String): Boolean {
        val timeProvider = SystemTimeProvider()
        val codeGenerator: CodeGenerator = DefaultCodeGenerator()
        val verifier = DefaultCodeVerifier(codeGenerator, timeProvider)
        return verifier.isValidCode(secret, code)
    }

    /**
     * Saves the secret securely (Placeholder function, modify as needed).
     */
    private fun saveHash(secret: String, context: Context) {
        // Implement secure storage (EncryptedSharedPreferences, Keystore, etc.)
        Log.d("SecureStorage", "Saving secret securely (not implemented)")
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

        */



        /*


        fun save2FAKey(userId: String, twoFAKey: String) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users/$userId")

            // Save the 2FA key under the user's ID
            userRef.child("2faKey").setValue(twoFAKey).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Log.d("2FA", "2FA key saved successfully.")
                } else {
                    //Log.e("2FA", "Failed to save 2FA key: ${task.exception?.message}")
                }
            }
        }


        fun get2FAKey(userId: String, callback: (String?) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users/$userId/2faKey")

            // Retrieve the 2FA key for the user
            userRef.get().addOnSuccessListener { snapshot ->
                val twoFAKey = snapshot.getValue(String::class.java)
                callback(twoFAKey)
            }.addOnFailureListener { exception ->
                Log.e("2FA", "Failed to retrieve 2FA key: ${exception.message}")
                callback(null)
            }
        }
        */



        



    }
}

