package io.openfuture.state.controller

import io.openfuture.state.domain.request.AddWalletsRequest
import io.openfuture.state.entity.Account
import io.openfuture.state.entity.Wallet
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
    fun add(@PathVariable accountId: Long, @RequestBody request: AddWalletsRequest): Account {
        return accountService.addWallets(accountId, request.integrations)
    }

    @GetMapping
    fun getAll(@PathVariable accountId: Long): List<Wallet> {
        return walletService.getAllByAccount(accountId)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable accountId: Long, @PathVariable id: Long): Wallet {
        return walletService.get(id, accountId)
    }

}
