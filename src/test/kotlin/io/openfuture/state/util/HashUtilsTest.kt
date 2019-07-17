package io.openfuture.state.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class HashUtilsTest {

    @Test
    fun encodeSha256eShouldReturnEncodedByteArray() {
        val encoded = HashUtils.sha256("test string".toByteArray())

        assertThat(encoded).isNotEmpty()
    }

    @Test
    fun encodeSha256eShouldReturnDifferentByteArrayForDifferentInput() {
        val encodedArray1 = HashUtils.sha256("test string1".toByteArray())
        val encodedArray2 = HashUtils.sha256("test string2".toByteArray())

        assertThat(encodedArray1.contentEquals(encodedArray2)).isFalse()
    }

    @Test
    fun merkleRootShouldReturnRootHashStringOfListOfHashStrings() {
        val hashString1 = "hash1"
        val hashString2 = "hash2"
        val hashString3 = "hash3"

        val merkleRootHash = HashUtils.merkleRoot(listOf(hashString1, hashString2, hashString3))

        assertThat(merkleRootHash).isNotEmpty()
    }

    @Test
    fun merkleRootShouldReturnHashStringWhenGetOneHashString() {
        val hashString1 = "hash1"

        val merkleRootHash = HashUtils.merkleRoot(listOf(hashString1))

        assertThat(merkleRootHash).isNotEmpty()
        assertThat(merkleRootHash).isEqualTo(hashString1)
    }

}
