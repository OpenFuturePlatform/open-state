package io.openfuture.state.service

import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.model.Blockchain
import io.openfuture.state.repository.WalletRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(private val repository: WalletRepository) : WalletService {

    override suspend fun save(address: String, webhook: String, blockchain: Blockchain): Wallet {
        val wallet = Wallet(address, webhook, blockchain)
        return repository.save(wallet).awaitSingle()
    }

    override suspend fun findByAddress(address: String): Wallet {
        return repository.findByAddress(address).awaitFirstOrNull()
                ?: throw NotFoundException("Wallet not found")
    }

    override suspend fun addTransactions(requests: List<AddTransactionRequest>) {
        requests.forEach {
            val wallet = findByAddress(it.walletAddress)
            val transaction = Transaction(
                    it.hash,
                    wallet.address,
                    it.amount,
                    it.fee,
                    it.date,
                    it.blockHeight,
                    it.blockHash
            )
            wallet.addTransaction(transaction)
            repository.save(wallet).awaitSingle()
        }
    }

    override suspend fun existsByAddressAndBlockchain(address: String, blockchain: Blockchain): Boolean {
        return repository.existsByAddressAndBlockchain(address, blockchain).awaitSingle()
    }
}
