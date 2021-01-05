package io.openfuture.state.util

 class MathUtils {

    companion object {

        fun fibonachi(n: Int): Long {
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
