package io.openfuture.state.controller

import io.openfuture.state.entity.Account
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.service.AccountService
import io.openfuture.state.service.WalletService
import io.openfuture.state.util.any
import io.openfuture.state.util.createDummyAccount
import io.openfuture.state.util.readResource
import org.junit.Test
import org.mockito.BDDMockito.anySet
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AccountController::class)
class AccountControllerTest : BaseControllerTest() {

    @MockBean
    private lateinit var accountService: AccountService

    @MockBean
    private lateinit var walletService: WalletService


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
    fun create_WhenAccountRequestAddressIsEmpty_ShouldReturnUnprocessableEntityStatus() {
        val requestBody = """
            {
              "webHook": "",
              "integrations": [
                {
                  "address": "",
                  "blockchainId": 1
            
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun getAccountByIdTest() {
        val account = createDummyAccount().apply { id = 1 }

        given(accountService.get(account.id)).willReturn(account)

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk)
    }

    @Test
    fun get_WhenAccountIsNotPresented_ShouldReturnNotFoundStatus() {

        given(accountService.get(any(Long::class.java))).willThrow(NotFoundException("Account with id 1 not found"))

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound)
    }

    @Test
    fun updateAccountWebHookTest() {
        val requestBody = readResource("updateAccountWebHookRequest.json", javaClass)
        val account = createDummyAccount().apply { id = 1 }

        given(accountService.update(account.id, "http://updated-webhook.com")).willReturn(account)
        given(walletService.getAllByAccount(account)).willReturn(account.wallets.toList())

        mockMvc.perform(put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk)
    }

    @Test
    fun update_WithInvalidUpdateRequest_ShouldReturnBadRequestStatus() {
        val requestBody = """
            {
              "id": 1,
              "webHook": ""
            }
        """.trimIndent()

        mockMvc.perform(put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun deleteAccountTest() {
        val accountId = 1L
        val account = createDummyAccount(isEnabled = false).apply { id = accountId }

        given(accountService.delete(accountId)).willReturn(account)

        mockMvc.perform(delete("/api/accounts/$accountId"))
                .andExpect(status().isOk)
    }

    @Test
    fun delete_WhenAccountIsNotPresented_ShouldReturn_NotFoundStatus() {
        given(accountService.delete(1L)).willThrow(NotFoundException("Account with id 1 not found"))

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNotFound)
    }

}
