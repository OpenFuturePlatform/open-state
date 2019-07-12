package io.openfuture.state.controller

import io.openfuture.state.domain.page.PageRequest
import io.openfuture.state.service.TransactionService
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyWallet
import org.junit.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TransactionController::class)
class TransactionControllerTest : BaseControllerTest() {

    @MockBean
    private lateinit var transactionService: TransactionService


    @Test
    fun getAllTransactionByWalletId() {
        val pageRequest = PageRequest()
        val wallet = createDummyWallet().apply { id = 1 }
        val transaction = createDummyTransaction(wallet = wallet)

        given(transactionService.getAllByWalletId(wallet.id, pageRequest)).willReturn(PageImpl(listOf(transaction)))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/wallets/1/transactions"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getTransactionByIdAndWalletId() {
        val wallet = createDummyWallet().apply { id = 1 }
        val transaction = createDummyTransaction(wallet = wallet).apply { id = 1 }

        given(transactionService.get(transaction.id, wallet.id)).willReturn(transaction)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/wallets/1/transactions/1"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

}
