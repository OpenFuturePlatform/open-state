package io.openfuture.state.util

import java.security.MessageDigest

object HashUtils {

    private const val SHA256 = "SHA-256"


    fun sha256(bytes: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(SHA256)
        digest.update(bytes, 0, bytes.size)
        return digest.digest()
    }

}