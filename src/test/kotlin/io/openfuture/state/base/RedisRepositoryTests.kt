package io.openfuture.state.base

import io.openfuture.state.config.RedisConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate

@DataRedisTest
@Import(RedisConfig::class, JacksonAutoConfiguration::class)
abstract class RedisRepositoryTests {

    @Autowired
    protected lateinit var redisTemplate: ReactiveRedisTemplate<String, Any>

}
