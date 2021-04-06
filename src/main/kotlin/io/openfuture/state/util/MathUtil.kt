package io.openfuture.state.util

class MathUtil {

    companion object {

        fun fb(n: Int): Long {
            var prev = 0L
            var current = 1L
            for (i in 2 until n) {
                val temp = prev + current
                prev = current
                current = temp
            }

            return current
        }
    }
}
