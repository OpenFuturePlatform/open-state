package io.openfuture.state.base

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

abstract class ControllerTests {

    @Autowired
    protected lateinit var webClient: WebTestClient

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

}
