package io.openfuture.state.controller

import io.openfuture.state.domain.dto.AccountDto
import io.openfuture.state.domain.dto.WalletDto
import io.openfuture.state.domain.request.AddWalletsRequest
import io.openfuture.state.service.AccountService
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts/{accountId}/wallets")
class WalletController(
        private val walletService: WalletService,
        private val accountService: AccountService
) {

    @PostMapping
    fun add(@PathVariable accountId: Long, @RequestBody request: AddWalletsRequest): AccountDto {
        val updatedAccount = accountService.addWallets(accountId, request.integrations)
        return AccountDto(updatedAccount)
    }

    @GetMapping
    fun getAll(@PathVariable accountId: Long): List<WalletDto> {
        val wallets = walletService.getAllByAccount(accountId)
        return wallets.map { WalletDto(it) }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable accountId: Long, @PathVariable id: Long): WalletDto {
        val wallet = walletService.get(id, accountId)
        return WalletDto(wallet)
    }

}
