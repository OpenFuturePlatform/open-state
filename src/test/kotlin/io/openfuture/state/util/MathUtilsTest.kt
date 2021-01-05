package io.openfuture.state.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class MathUtilsTest {

    @Test
    fun fibonachiShouldReturnSecondElementOne() {
        val value = MathUtils.fibonachi(2)

        Assertions.assertThat(value).isEqualTo(1)
    }

    @Test
    fun fibonachiShouldReturnFifthElementThree() {
        val value = MathUtils.fibonachi(5)

        Assertions.assertThat(value).isEqualTo(3)
    }
}
