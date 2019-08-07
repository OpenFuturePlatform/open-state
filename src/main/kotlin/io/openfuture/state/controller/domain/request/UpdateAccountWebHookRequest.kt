package io.openfuture.state.controller.domain.request

import org.apache.commons.validator.routines.UrlValidator
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateAccountWebHookRequest(
        @field:NotNull var id: Long,
        @field:NotBlank var webHook: String
) {

    @AssertTrue(message = "Invalid web hook url")
    fun isWebHook(): Boolean {
        return UrlValidator().isValid(webHook)
    }

}
