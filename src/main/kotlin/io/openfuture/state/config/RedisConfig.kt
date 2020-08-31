package io.openfuture.state.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.openfuture.state.model.BlockchainType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext

@Configuration
class RedisConfig {

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory, objectMapper: ObjectMapper): ReactiveRedisTemplate<BlockchainType, Long> {
        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)
        val serializationContext = RedisSerializationContext.newSerializationContext<BlockchainType, Long>(serializer).build()
        return ReactiveRedisTemplate(factory, serializationContext)
    }

}
