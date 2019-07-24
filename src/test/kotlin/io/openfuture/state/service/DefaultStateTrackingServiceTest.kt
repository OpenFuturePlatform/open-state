package io.openfuture.state.service

import io.openfuture.state.webhook.WebhookSender
import io.openfuture.state.entity.State
import io.openfuture.state.entity.Transaction
import io.openfuture.state.util.*
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class DefaultStateTrackingServiceTest {

    private val stateService = mock(StateService::class.java)
    private val walletService = mock(WalletService::class.java)
    private val transactionService = mock(TransactionService::class.java)
    private val webhookSender = mock(WebhookSender::class.java)

    private lateinit var stateTrackingService: StateTrackingService


    @Before
    fun setUp() {
        stateTrackingService = DefaultStateTrackingService(stateService, walletService, transactionService, webhookSender)
    }

    @Test
    fun processTransactionShouldSaveTransactionsAndUpdateWalletStatesIfWalletsExist() {
        val transactionDto = createDummyTransactionDto()
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val transaction = createDummyTransaction()

        val stateFromWallet = createDummyState().apply { id = 1 }
        val fromWallet = createDummyWallet(state = stateFromWallet, blockchain = blockchain, address = transactionDto.from)

        val stateToWallet = createDummyState().apply { id = 2 }
        val toWallet = createDummyWallet(state = stateToWallet, blockchain = blockchain, address = transactionDto.to).apply { id = 2 }

        given(walletService.getActiveByBlockchainAddress(fromWallet.blockchain.id, fromWallet.address)).willReturn(fromWallet)
        given(transactionService.save(any(Transaction::class.java))).willReturn(transaction)
        given(stateService.get(stateFromWallet.id)).willReturn(stateFromWallet)
        given(stateService.save(stateFromWallet)).willReturn(stateFromWallet)

        given(walletService.getActiveByBlockchainAddress(toWallet.blockchain.id, toWallet.address)).willReturn(toWallet)
        given(transactionService.save(any(Transaction::class.java))).willReturn(transaction)
        given(stateService.get(stateToWallet.id)).willReturn(stateToWallet)
        given(stateService.save(stateToWallet)).willReturn(stateToWallet)

        stateTrackingService.processTransaction(transactionDto)
    }

    @Test
    fun processTransactionShouldIgnoreTransactionIfWalletsDoNotExists() {
        val transactionDto = createDummyTransactionDto()

        given(walletService.getActiveByBlockchainAddress(transactionDto.blockchainId, transactionDto.from)).willReturn(null)
        given(walletService.getActiveByBlockchainAddress(transactionDto.blockchainId, transactionDto.to)).willReturn(null)

        verify(transactionService, never()).save(any(Transaction::class.java))
        verify(stateService, never()).get(transactionDto.blockchainId)
        verify(stateService, never()).save(any(State::class.java))

        stateTrackingService.processTransaction(transactionDto)
    }

}
