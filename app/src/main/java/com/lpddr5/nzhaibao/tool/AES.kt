package com.lpddr5.nzhaibao.tool

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun aesEnc(input: String, password: String = "1696547463138478"): String {
    val cipher = Cipher.getInstance("AES")
    val keySpec = SecretKeySpec(password.toByteArray(), "AES")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    val encrypt = cipher.doFinal(input.toByteArray())
    return Base64.encodeToString(encrypt, Base64.NO_WRAP)
}

fun aesDec(input: String, password: String = "1696547463138478"): String {
    val cipher = Cipher.getInstance("AES")
    val keySpec = SecretKeySpec(password.toByteArray(), "AES")
    cipher.init(Cipher.DECRYPT_MODE, keySpec)
    val encrypt = cipher.doFinal(Base64.decode(input, Base64.NO_WRAP))
    return String(encrypt)
}
