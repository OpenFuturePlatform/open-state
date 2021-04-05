package io.openfuture.state.util

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


fun mockPost(webClient: WebClient, response: Any) {
    val post = mock<WebClient.RequestBodyUriSpec>()
    val uri = mock<WebClient.RequestBodySpec>()
    val responseSpec = mock<WebClient.ResponseSpec>()

    given(webClient.post()).willReturn(post)
    given(post.contentType(MediaType.APPLICATION_JSON)).willReturn(uri)
    given(uri.body(any())).willReturn(uri)
    given(uri.retrieve()).willReturn(responseSpec)
    given(responseSpec.bodyToMono(any<ParameterizedTypeReference<Any>>())).willReturn(Mono.just(response))
}

fun mockBuilder(builder: WebClient.Builder, webClient: WebClient) {
    given(builder.codecs(any())).willReturn(builder)
    given(builder.baseUrl(any())).willReturn(builder)
    given(builder.defaultHeaders(any())).willReturn(builder)
    given(builder.build()).willReturn(webClient)
}
