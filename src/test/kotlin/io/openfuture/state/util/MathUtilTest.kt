package io.openfuture.state.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class MathUtilTest {

    @Test
    fun fbShouldReturnSecondElementOne() {
        val value = MathUtil.fb(2)

        Assertions.assertThat(value).isEqualTo(1)
    }

    @Test
    fun fbShouldReturnFifthElementThree() {
        val value = MathUtil.fb(5)

        Assertions.assertThat(value).isEqualTo(3)
    }
}
