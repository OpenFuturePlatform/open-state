package io.openfuture.state.webhook

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.UnknownHostException

@Component
class WebhookRestClient(private val webClient: WebClient) {

    suspend fun doGet(url: String): WebhookResponse {
        return try {
            val response = webClient.get()
                    .uri(url)
                    .exchange()
                    .awaitSingle()

            WebhookResponse(response.statusCode(), response.statusCode().reasonPhrase)
        } catch (ex: UnknownHostException) {
            WebhookResponse(HttpStatus.NOT_FOUND, "Host could not be determined")
        } catch (ex: Exception) {
            WebhookResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }
    }

    suspend fun doPost(url: String, body: Any): WebhookResponse {
        return try {
            val response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .exchange()
                    .awaitSingle()

            WebhookResponse(response.statusCode(), response.statusCode().reasonPhrase)
        } catch (ex: UnknownHostException) {
            WebhookResponse(HttpStatus.NOT_FOUND, "Host could not be determined")
        } catch (ex: Exception) {
            WebhookResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }

    }
    
}
