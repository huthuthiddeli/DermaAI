    ---
config:
  theme: default
---
classDiagram
    class Authentication {
        +generateSecret(context: Context): String
        +validateTOTP(secret: String, code: String): Boolean
        +saveHash(hash: String, context: Context)
        +saveEncryptedHashInKeystore(context: Context, keyAlias: String, hash: String)
        -getSecretKeyFromKeystore(keyAlias: String): SecretKey?
        +retrieveEncryptedHashFromKeystore(context: Context, keyAlias: String): String?
    }