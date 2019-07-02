package io.openfuture.state.controller

import io.openfuture.state.domain.request.CreateAccountRequest
import io.openfuture.state.domain.request.UpdateAccountWebHookRequest
import io.openfuture.state.entity.Account
import io.openfuture.state.service.AccountService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
        private val accountService: AccountService
) {

    @PostMapping
    fun create(@RequestBody request: CreateAccountRequest) {
        accountService.create(request.webHook, request.integrations)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Account {
        return accountService.get(id)
    }

    @PutMapping
    fun update(@RequestBody request: UpdateAccountWebHookRequest): Account {
        return accountService.update(request.id, request.webHook)
    }

}
