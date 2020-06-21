package io.openfuture.state.controller

import io.openfuture.state.service.StateTrackingService
import org.junit.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TrackingController::class)
class TrackingControllerTest : BaseControllerTest() {

    @MockBean
    private lateinit var stateTrackingService: StateTrackingService

    @Test
    fun processTransaction_ShouldCallStateTrackingService() {
        val request = """
            {
              "blockchainId": 1,
              "hash": "",
              "from": "",
              "to": "",
              "amount": 1,
              "fee": 1,
              "date": 1,
              "blockHeight": 1,
              "blockHash": 1
            }
        """.trimIndent()

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request))
                .andExpect(status().isOk)
    }

}