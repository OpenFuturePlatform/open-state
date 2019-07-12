package io.openfuture.state.controller

import io.openfuture.state.service.AccountService
import io.openfuture.state.service.WalletService
import io.openfuture.state.util.createDummyAccount
import io.openfuture.state.util.createDummyWallet
import io.openfuture.state.util.readResource
import org.junit.Test
import org.mockito.ArgumentMatchers.anySet
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(WalletController::class)
class
WalletControllerTest : BaseControllerTest() {

    @MockBean
    private lateinit var walletService: WalletService

    @MockBean
    private lateinit var accountService: AccountService


    @Test
    fun add() {
        val requestBody = readResource("addWalletRequest.json", javaClass)
        val account = createDummyAccount().apply { id = 1 }

        given(accountService.addWallets(anyLong(), anySet())).willReturn(account)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getAllByAccountIdTest() {
        val wallet = createDummyWallet()
        val account = createDummyAccount(wallets = mutableSetOf(wallet)).apply { id = 1 }

        given(accountService.get(account.id)).willReturn(account)
        given(walletService.getAllByAccount(account)).willReturn(listOf(wallet))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/1/wallets"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun get() {
        val wallet = createDummyWallet().apply { id = 1 }
        val account = createDummyAccount(wallets = mutableSetOf(wallet)).apply { id = 1 }

        given(accountService.get(account.id)).willReturn(account)
        given(walletService.get(wallet.id, account)).willReturn(wallet)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/1/wallets/1"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

}
