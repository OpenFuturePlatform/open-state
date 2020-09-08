package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(private val repository: WalletRepository) : WalletService {

    override suspend fun save(blockchain: Blockchain, address: String, webhook: String): Wallet {
        val wallet = Wallet(address, webhook, blockchain.getName())
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

    override suspend fun existsByBlockchainAndAddress(blockchain: Blockchain, address: String): Boolean {
        return repository.existsByBlockchainAndAddress(blockchain.getName(), address).awaitSingle()
    }
}
