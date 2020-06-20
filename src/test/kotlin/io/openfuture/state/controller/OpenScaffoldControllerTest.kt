package io.openfuture.state.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.openfuture.state.controller.domain.request.SaveOpenScaffoldRequest
import io.openfuture.state.entity.OpenScaffold
import io.openfuture.state.service.DefaultOpenScaffoldService
import org.junit.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OpenScaffoldController::class)
class OpenScaffoldControllerTest : BaseControllerTest() {

    @MockBean
    private lateinit var openScaffoldService: DefaultOpenScaffoldService
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun save_WhenRequestIsValid_ShouldReturnIsOkStatusCode() {
        val request = SaveOpenScaffoldRequest(
                address = "Address",
                webHook = "https://url.com"
        )
        val expected = OpenScaffold("Address", "https://url.com")
                .apply { id = 1L }

        given(openScaffoldService.save(request)).willReturn(expected)

        mockMvc.perform(post("/api/open-scaffolds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk)
    }

    @Test
    fun save_WhenRequestAddressIsEmpty_ShouldReturnBadRequestStatus() {
        val request = """
            {
              "address": "",
              "webHook": "https://url.com"
            }         
        """.trimIndent()

        mockMvc.perform(post("/api/open-scaffolds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun save_WhenRequestWebHookIsEmpty_ShouldReturnBadRequestStatus() {
        val request = """
            {
              "address": "some_address",
              "webHook": ""
            }         
        """.trimIndent()

        mockMvc.perform(post("/api/open-scaffolds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest)
    }

}