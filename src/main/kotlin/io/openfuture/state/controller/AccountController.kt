package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.AccountDto
import io.openfuture.state.controller.domain.request.CreateAccountRequest
import io.openfuture.state.controller.domain.request.UpdateAccountWebHookRequest
import io.openfuture.state.entity.Account
import io.openfuture.state.service.AccountService
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/accounts")
class AccountController(
        private val accountService: AccountService,
        private val walletService: WalletService
) {

    @PostMapping
    fun create(@RequestBody @Valid request: CreateAccountRequest): AccountDto {
        val account = accountService.save(Account(request.webHook), request.integrations)
        return AccountDto(account)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): AccountDto {
        val account = accountService.get(id)
        account.wallets = walletService.getAllByAccount(account).toMutableSet()
        return AccountDto(account)
    }

    @PutMapping
    fun update(@RequestBody @Valid request: UpdateAccountWebHookRequest): AccountDto {
        val updatedAccount = accountService.update(request.id, request.webHook)
        updatedAccount.wallets = walletService.getAllByAccount(updatedAccount).toMutableSet()
        return AccountDto(updatedAccount)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): AccountDto {
        val deletedAccount = accountService.delete(id)
        return AccountDto(deletedAccount)
    }

}
