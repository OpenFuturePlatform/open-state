package io.openfuture.state.webhook

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@Component
class WebhookRestClient(builder: WebClient.Builder) {

    private val client: WebClient = builder.build()


    suspend fun doPost(url: String, body: Any): WebhookResponse {
        println("webhook body $body")
        return try {
            val response = client.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .exchange()
                .awaitSingle()

            WebhookResponse(response.statusCode(), url, response.statusCode().reasonPhrase)
        } catch (ex: UnknownHostException) {
            WebhookResponse(HttpStatus.NOT_FOUND, url, "Host could not be determined")
        } catch (ex: Exception) {
            WebhookResponse(HttpStatus.INTERNAL_SERVER_ERROR, url, ex.message)
        }
    }

    suspend fun doPostWoocommerce(url: String, signature: String, woocommerceDto: WebhookPayloadDto.WebhookWoocommerceDto): WebhookResponse {
        return try {
            val response = client.post()
                .uri(url)
                .header("HTTP_X_OPEN_WEBHOOK_SIGNATURE",signature)
                .header("HTTP_X_OPEN_WEBHOOK_TIMESTAMP", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(woocommerceDto))
                .exchange()
                .awaitSingle()

            WebhookResponse(response.statusCode(), url, response.statusCode().reasonPhrase)
        } catch (ex: UnknownHostException) {
            WebhookResponse(HttpStatus.NOT_FOUND, url, "Host could not be determined")
        } catch (ex: Exception) {
            WebhookResponse(HttpStatus.INTERNAL_SERVER_ERROR, url, ex.message)
        }
    }

    data class WebhookResponse(
        val status: HttpStatus,
        val url: String,
        val message: String?
    )

}
