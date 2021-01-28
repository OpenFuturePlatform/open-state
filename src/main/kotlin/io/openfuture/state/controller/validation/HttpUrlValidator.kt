package io.openfuture.state.controller.validation

import org.apache.commons.validator.routines.UrlValidator
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class HttpUrlValidator : ConstraintValidator<HttpUrl, String> {

    private val validator = UrlValidator(arrayOf("http", "https"))

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        return validator.isValid(value)
    }
}
