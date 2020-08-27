package io.openfuture.state.service

import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.controller.domain.request.SaveWalletRequest
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.mapper.WalletMapper
import io.openfuture.state.model.Wallet
import io.openfuture.state.repository.WalletRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WalletServiceImpl(private val walletRepository: WalletRepository,
                        private val walletMapper: WalletMapper) : WalletService {

    override suspend fun save(request: SaveWalletRequest): WalletDto {
        val wallet = Wallet(
                address = request.address,
                webhook = request.webhook,
                lastUpdateDate = LocalDateTime.now(),
                transactions = emptySet()
        )
        return walletMapper.toWalletDto(walletRepository.save(wallet).awaitSingle())
    }

    override suspend fun findByAddress(address: String): WalletDto {
        val wallet = walletRepository.findByAddress(address).awaitFirstOrNull()
        wallet ?: throw NotFoundException("Wallet with address $address not found")
        return walletMapper.toWalletDto(wallet)
    }

}
