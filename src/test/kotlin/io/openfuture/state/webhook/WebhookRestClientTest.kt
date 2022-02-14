package io.openfuture.state.webhook

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class WebhookRestClientTest {

    private val webClient: WebClient = mock()
    private val builder: WebClient.Builder = mock()
    private lateinit var webhookRestClient: WebhookRestClient

    @BeforeEach
    fun setUp() {
        given(builder.build()).willReturn(webClient)
        webhookRestClient = WebhookRestClient(builder)
    }

    @Test
    fun doPostShouldReturnWebhookResponse() = runBlocking<Unit> {
        val url = "https://example.com"
        val post = mock<WebClient.RequestBodyUriSpec>()
        val clientResponse: ClientResponse = mock()
        val expected = WebhookRestClient.WebhookResponse(HttpStatus.OK, url, HttpStatus.OK.reasonPhrase)

        given(clientResponse.statusCode()).willReturn(HttpStatus.OK)
        given(webClient.post()).willReturn(post)
        given(post.uri(url)).willReturn(post)
        given(post.contentType(MediaType.APPLICATION_JSON)).willReturn(post)
        given(post.body(any())).willReturn(post)
        given(post.exchange()).willReturn(Mono.just(clientResponse))

        val result = webhookRestClient.doPost(url, "body")

        assertThat(result).isEqualTo(expected)
    }

}
