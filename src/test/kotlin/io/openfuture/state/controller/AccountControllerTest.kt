package io.openfuture.state.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.openfuture.state.entity.Account
import io.openfuture.state.service.AccountService
import io.openfuture.state.service.WalletService
import io.openfuture.state.util.any
import io.openfuture.state.util.createDummyAccount
import io.openfuture.state.util.readResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.anySet
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest(AccountController::class)
@RunWith(SpringRunner::class)
class AccountControllerTest {

    val objectMapper: ObjectMapper = jacksonObjectMapper()

    @MockBean
    private lateinit var accountService: AccountService

    @MockBean
    private lateinit var walletService: WalletService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext


    @Before
    fun initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun createAccountTest() {
        val requestBody = readResource("createAccountRequest.json", javaClass)
        val account = createDummyAccount().apply { id = 1 }

        given(accountService.save(any(Account::class.java), anySet())).willReturn(account)

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk)
    }

    @Test
    fun get() {
        val account = createDummyAccount().apply { id = 1 }

        given(accountService.get(account.id)).willReturn(account)

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk)
    }

    @Test
    fun update() {
    }

}
