package io.openfuture.state.extension

import org.springframework.data.redis.core.ReactiveRedisTemplate
import java.nio.ByteBuffer

fun ReactiveRedisTemplate<String, Any>.serialize(value: String): ByteBuffer {
    return this.serializationContext.keySerializationPair.write(value)
}
