package io.openfuture.state.util

import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest

object HashUtils {

    private const val SHA256 = "SHA-256"

    private const val ENCODED_ZERO = '1'
    private const val CHECKSUM_SIZE = 4

    private const val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private val alphabetIndices by lazy {
        IntArray(128) { alphabet.indexOf(it.toChar()) }
    }

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

        return Hex.encodeHexString(sha256(previousTreeLayout[0] + previousTreeLayout[1]))
    }

    @Throws(NumberFormatException::class)
    fun String.decodeBase58(): ByteArray {
        if (isEmpty()) {
            return ByteArray(0)
        }
        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
        val input58 = ByteArray(length)
        for (i in indices) {
            val c = this[i]
            val digit = if (c.code < 128) alphabetIndices[c.code] else -1
            if (digit < 0) {
                throw NumberFormatException("Illegal character $c at position $i")
            }
            input58[i] = digit.toByte()
        }
        // Count leading zeros.
        var zeros = 0
        while (zeros < input58.size && input58[zeros].toInt() == 0) {
            ++zeros
        }
        // Convert base-58 digits to base-256 digits.
        val decoded = ByteArray(length)
        var outputStart = decoded.size
        var inputStart = zeros
        while (inputStart < input58.size) {
            decoded[--outputStart] = divmod(input58, inputStart.toUInt(), 58.toUInt(), 256.toUInt()).toByte()
            if (input58[inputStart].toInt() == 0) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.size && decoded[outputStart].toInt() == 0) {
            ++outputStart
        }
        // Return decoded data (including original number of leading zeros).
        return decoded.copyOfRange(outputStart - zeros, decoded.size)
    }

    private fun divmod(number: ByteArray, firstDigit: UInt, base: UInt, divisor: UInt): UInt {
        // this is just long division which accounts for the base of the input digits
        var remainder = 0.toUInt()
        for (i in firstDigit until number.size.toUInt()) {
            val digit = number[i.toInt()].toUByte()
            val temp = remainder * base + digit
            number[i.toInt()] = (temp / divisor).toByte()
            remainder = temp % divisor
        }
        return remainder
    }

    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}
