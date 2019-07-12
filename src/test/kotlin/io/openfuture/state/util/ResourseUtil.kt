package io.openfuture.state.util

fun readResource(filename: String, javaClass: Class<*>): String {
    javaClass.getResourceAsStream(filename).bufferedReader().use {
        return it.readText()
    }

}
