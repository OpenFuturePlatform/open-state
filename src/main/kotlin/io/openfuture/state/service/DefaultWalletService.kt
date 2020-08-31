package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.TransactionRequest
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(private val walletRepository: WalletRepository) : WalletService {

    override suspend fun save(address: String, webhook: String): Wallet {
        val wallet = Wallet(address, webhook)
        return walletRepository.save(wallet).awaitSingle()
    }

    override suspend fun findByAddress(address: String): Wallet {
        return walletRepository.findByAddress(address).awaitFirstOrNull()
                ?: throw NotFoundException("Wallet not found")
    }

    override suspend fun addTransactions(requests: Set<TransactionRequest>) {
        requests.forEach {
            val wallet = findByAddress(it.walletAddress)
            val transaction = Transaction(
                    blockchainType = it.blockChainType,
                    hash = it.hash,
                    participant = wallet.address,
                    amount = it.amount,
                    fee = it.fee,
                    date = it.date,
                    blockHeight = it.blockHeight,
                    blockHash = it.blockHash
            )
            wallet.transactions.add(transaction)
            walletRepository.save(wallet).awaitSingle()
        }
    }

    override suspend fun existsByAddress(address: String): Boolean {
        return walletRepository.existsByAddress(address).awaitSingle()
    }
}
