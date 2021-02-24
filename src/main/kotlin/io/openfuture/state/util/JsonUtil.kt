package io.openfuture.state.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

class JsonUtil {

    companion object {

        fun <T> fromJson(ы: String?, valueClass: Class<T>?): T {
            return createDefaultMapper().readValue(ы, valueClass)
        }

        fun toJson(o: Any): String = createDefaultMapper().writeValueAsString(o)

        private fun createDefaultMapper(): ObjectMapper {
            val mapper = ObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper
        }
    }
}
