package io.openfuture.state.controller.domain.request

import org.apache.commons.validator.routines.UrlValidator
import javax.validation.constraints.AssertTrue

data class CreateAccountRequest(
        val webHook: String,
        val integrations: Set<CreateIntegrationRequest>
) {

    @AssertTrue(message = "Invalid web hook url")
    fun isWebHook(): Boolean {
        if (webHook.isEmpty()) return true

        return UrlValidator().isValid(webHook)
    }

}
