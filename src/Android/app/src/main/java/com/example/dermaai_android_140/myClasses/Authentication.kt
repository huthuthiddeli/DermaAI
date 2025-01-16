package com.example.dermaai_android_140.myClasses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import dev.samstevens.totp.code.CodeGenerator
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import android.util.Base64
import java.security.KeyPair
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class Authentication {

    fun generateSecret(context: Context) : String
    {
        val secretGenerator : SecretGenerator = DefaultSecretGenerator();
        val secret = secretGenerator.generate();

        saveHash(secret, context)

        return secret
    }


    fun qr(secret : String, email : String)
    {
        /*
        val data = QrData.Builder()
            .label(email)
            .secret(secret)
            .issuer("DermaAI")
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build()


        val generator: QrGenerator = ZxingPngQrGenerator()
        val imageData: ByteArray = generator.generate(data)
        */

        /*
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("TOTP Secret", secret)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "TOTP copied", Toast.LENGTH_SHORT).show()*/
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
                .setUserAuthenticationRequired(false) // Set to true if you want biometric or PIN protection
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
        val editor = sharedPreferences.edit()

        // Convert IV and encrypted hash to Base64 strings
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        val encryptedHashBase64 = Base64.encodeToString(encryptedHash, Base64.DEFAULT)

        // Save to SharedPreferences
        editor.putString("${keyAlias}_iv", ivBase64)
        editor.putString("${keyAlias}_hash", encryptedHashBase64)
        editor.apply()

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

