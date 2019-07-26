package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.AccountDto
import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.controller.domain.request.AddWalletsRequest
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
        val account = accountService.get(accountId)
        val wallets = walletService.getAllByAccount(account)
        return wallets.map { WalletDto(it) }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable accountId: Long, @PathVariable id: Long): WalletDto {
        val account = accountService.get(accountId)
        val wallet = walletService.get(id, account)
        return WalletDto(wallet)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable accountId: Long, @PathVariable id: Long): AccountDto {
        val account = accountService.deleteWallet(accountId, id)
        return AccountDto(account)
    }

}
