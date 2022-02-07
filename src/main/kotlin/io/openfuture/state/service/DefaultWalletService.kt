package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.client.BinanceHttpClientApi
import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.repository.WalletRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class DefaultWalletService(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val webhookService: WebhookService,
    private val webhookInvoker: WebhookInvoker,
    private val binanceHttpClientApi: BinanceHttpClientApi
) : WalletService {

    override suspend fun findByIdentity(blockchain: String, address: String): Wallet {
        val identity = WalletIdentity(blockchain, address)
        return walletRepository.findByIdentity(identity).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $blockchain - $address")
    }

    override suspend fun findById(id: String): Wallet {
        return walletRepository.findById(id).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $id")
    }

    override suspend fun create(blockchain: Blockchain, request: WalletController.SaveWalletRequest): Wallet {
        val price = binanceHttpClientApi.getEthereumRate().price
        val rate = BigDecimal.ONE.divide(price, price.scale(), RoundingMode.HALF_UP)

        val wallet = Wallet(
            WalletIdentity(blockchain.getName(), request.address), request.webhook,
            orderId = request.metadata.orderId,
            amount = request.metadata.amount.multiply(rate),
            orderKey = request.metadata.orderKey,
            productCurrency = request.metadata.productCurrency,
            source = request.metadata.source,
            paymentCurrency = request.metadata.paymentCurrency,
            rate = rate
        )
        return walletRepository.save(wallet).awaitSingle()
    }

    override suspend fun update(walletId: String, webhook: String): Wallet {
        val wallet = walletRepository.findById(walletId).awaitFirstOrNull() ?: throw NotFoundException("Wallet not found")
        if (webhook != wallet.webhook) {
            wallet.let {
                it.webhookStatus = WebhookStatus.OK
                it.webhook = webhook
            }
        }

        walletRepository.save(wallet).awaitSingle()
        if (wallet.webhookStatus == WebhookStatus.OK) {
            webhookService.scheduleTransactionsFromDeadQueue(wallet)
        }

        return wallet
    }

    override suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock) {
        for (transaction in block.transactions) {
            val identity = WalletIdentity(blockchain.getName(), transaction.to)
            val wallet = walletRepository.findByIdentity(identity.blockchain, identity.address).awaitFirstOrNull()

            wallet?.let { saveTransaction(it, block, transaction) }
        }
    }

    override suspend fun updateWebhookStatus(wallet: Wallet, status: WebhookStatus) {
        walletRepository.save(wallet.apply { webhookStatus = status }).awaitSingle()
    }

    private suspend fun saveTransaction(wallet: Wallet, block: UnifiedBlock, unifiedTransaction: UnifiedTransaction) {
        val transaction = Transaction(
            wallet.identity,
            unifiedTransaction.hash,
            unifiedTransaction.from,
            unifiedTransaction.to,
            unifiedTransaction.amount,
            block.date,
            block.number,
            block.hash
        )

        transactionRepository.save(transaction).awaitSingle()
        log.info("Saved transaction ${transaction.id}")
        wallet.totalPaid = wallet.totalPaid.add(transaction.amount)
        val updatedWallet = walletRepository.save(wallet).awaitSingle()
        webhookInvoker.invoke(updatedWallet, transaction)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}
