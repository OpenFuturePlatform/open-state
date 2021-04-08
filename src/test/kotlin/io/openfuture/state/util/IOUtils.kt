package io.openfuture.state.util

inline fun <reified T> readResource(path: String): String =
    T::class.java.getResourceAsStream(path).bufferedReader().use {
        it.readText()
    }
