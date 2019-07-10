package io.openfuture.state.util

import org.mockito.Mockito

fun <T> any(clazz: Class<T>): T = Mockito.any<T>(clazz)