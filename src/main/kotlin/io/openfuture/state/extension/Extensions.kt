package io.openfuture.state.extension

import org.springframework.data.redis.core.ReactiveRedisTemplate
import java.nio.ByteBuffer

fun ReactiveRedisTemplate<String, Any>.serializeKey(value: String): ByteBuffer {
    return this.serializationContext.keySerializationPair.write(value)
}

fun ReactiveRedisTemplate<String, Any>.serializeValue(value: Any): ByteBuffer {
    return this.serializationContext.valueSerializationPair.write(value)
}
