package com.redsystemstudio.appcomprayventa.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object SignatureUtils {
    fun generarFirma(apiKey: String, merchantId: String, referenceCode: String, amount: String, currency: String): String {
        val cadenaParaFirmar = "$apiKey~$merchantId~$referenceCode~$amount~$currency"
        return try {
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(cadenaParaFirmar.toByteArray())
            val sb = StringBuilder()
            for (b in bytes) {
                sb.append(String.format("%02x", b))
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}
