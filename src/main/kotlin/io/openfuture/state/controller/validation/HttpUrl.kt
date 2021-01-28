package io.openfuture.state.controller.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [(HttpUrlValidator::class)])
annotation class HttpUrl(
        val message: String = "Not valid Http URL",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
