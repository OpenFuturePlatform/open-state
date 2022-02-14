package io.openfuture.state.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConfigurationProperties(prefix = "openapi")
@Validated
@Component
data class OpenApiProperties(@field:NotNull @field:NotBlank var baseUrl: String?)
