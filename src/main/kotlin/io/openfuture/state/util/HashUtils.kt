package io.openfuture.state.util

import org.apache.tomcat.util.buf.HexUtils
import java.security.MessageDigest

object HashUtils {

    private const val SHA256 = "SHA-256"


    fun sha256(bytes: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(SHA256)
        digest.update(bytes, 0, bytes.size)
        return digest.digest()
    }

    fun merkleRoot(hashes: List<String>): String {
        if (hashes.size == 1) {
            return hashes.single()
        }

        var previousTreeLayout = hashes.asSequence().sortedByDescending { it }.map { it.toByteArray() }.toList()
        var treeLayout = mutableListOf<ByteArray>()
        while (previousTreeLayout.size != 2) {
            for (i in 0 until previousTreeLayout.size step 2) {
                val leftHash = previousTreeLayout[i]
                val rightHash = if (i + 1 == previousTreeLayout.size) {
                    previousTreeLayout[i]
                } else {
                    previousTreeLayout[i + 1]
                }
                treeLayout.add(sha256(leftHash + rightHash))
            }
            previousTreeLayout = treeLayout
            treeLayout = mutableListOf()
        }

        return HexUtils.toHexString(sha256(previousTreeLayout[0] + previousTreeLayout[1]))
    }

}
