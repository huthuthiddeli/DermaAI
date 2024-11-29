package com.example.dermaai_android_140.myClasses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import dev.samstevens.totp.code.CodeGenerator
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider

class Authentication {

    fun generateSecret() : String
    {
        val secretGenerator : SecretGenerator = DefaultSecretGenerator();
        val secret = secretGenerator.generate();

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
}

