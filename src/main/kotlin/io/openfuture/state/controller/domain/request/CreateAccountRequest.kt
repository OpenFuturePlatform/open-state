package io.openfuture.state.controller.domain.request

import org.apache.commons.validator.routines.UrlValidator
import javax.validation.Valid
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotEmpty

data class CreateAccountRequest(
        val webHook: String,
        @field:NotEmpty var integrations: Set<@Valid CreateIntegrationRequest>
) {

    @AssertTrue(message = "Invalid web hook url")
    fun isWebHook(): Boolean {
        if (webHook.isEmpty()) return true

        return UrlValidator().isValid(webHook)
    }

}
