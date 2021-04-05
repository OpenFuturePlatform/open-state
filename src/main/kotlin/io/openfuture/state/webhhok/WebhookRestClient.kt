package io.openfuture.state.webhhok

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.UnknownHostException

@Component
class WebhookRestClient(private val webhookWebClient: WebClient) {

    suspend fun doPost(url: String, body: Any): WebhookResponse {
        return try {
            val response = webhookWebClient.post()
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

    data class WebhookResponse(
            val status: HttpStatus,
            val url: String,
            val message: String?
    )
}
