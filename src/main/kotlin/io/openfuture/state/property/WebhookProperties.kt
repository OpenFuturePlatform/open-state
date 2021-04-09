package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "webhook-invocation")
data class WebhookProperties(

    /**
     * Delay between attempts to execute scheduled webhooks
     * in wallets queue in seconds (default 5 sec)
     */
    val invocationProcessDelay: Duration = Duration.ofSeconds(5),

    /**
     * Lock timeout for wallets that process webhook
     * invocation in seconds. During that time another
     * instances can't process locked wallet
     * (default 300 sec)
     */
    val lockTTL: Duration = Duration.ofSeconds(300)
        /**
         * Lock timeout for wallets that process webhook
         * invocation in seconds. During that time another
         * instances can't process locked wallet
         * (default 300 sec)
         */
        val lockTTL: Duration = Duration.ofSeconds(300),

        /**
         * Options to configure webhook invocation retry in
         * case if previous attempts of invocation are failed
         */
        val retryOptions: RetryOptions = RetryOptions()
) {
        @Bean
        fun webhookInvocationProcessDelay(): Long {
                return invocationProcessDelay.toMillis()
        }

        fun maxRetryAttempts(): Int = retryOptions.progressiveMaxAttempts + retryOptions.dailyMaxAttempts


        data class RetryOptions(

                /**
                 * Max count of retry attempts for webhook
                 * invocation that use Fibonachi row to
                 * calculate delays between attempts (default 10 times)
                 */
                val progressiveMaxAttempts: Int = 10,

                /**
                 * Max count of attempts that invoked once
                 * per day after fbMaxAttempts are reached
                 * (default 17 days)
                 */
                val dailyMaxAttempts: Int = 17
        )
    @Bean
    fun webhookInvocationProcessDelay(): Long {
        return invocationProcessDelay.toMillis()
    }
}
