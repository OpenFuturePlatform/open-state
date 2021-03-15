package io.openfuture.state.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.stereotype.Component

@Component
class JsonSerializer {

    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())


    fun <T> fromJson(s: String, valueClass: Class<T>?): T {
        return objectMapper.readValue(s, valueClass)
    }

    fun toJson(o: Any): String = objectMapper.writeValueAsString(o)
}
