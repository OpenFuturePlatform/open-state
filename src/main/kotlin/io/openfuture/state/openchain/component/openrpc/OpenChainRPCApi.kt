package io.openfuture.state.openchain.component.openrpc

import io.openfuture.state.openchain.component.openrpc.dto.balance.BalanceResponseDto
import io.openfuture.state.openchain.component.openrpc.dto.transfertransaction.TransferTransactionListResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@FeignClient(name = "OpenChainRPCApi", url = "\${openfuture.chain.rpc-url}")
interface OpenChainRPCApi {

    @RequestMapping("/accounts/wallets/{walletAddress}/balance", method = [RequestMethod.GET])
    fun getWalletBalance(@PathVariable("walletAddress") walletAddress: String): BalanceResponseDto

    @GetMapping("/transactions/transfer", params = ["limit", "offset", "sortBy", "sortDirection"])
    fun getAllTransferTransactions(@RequestParam("limit") limit: Int = 1,
                                   @RequestParam("offset") offset: Long = 0,
                                   @RequestParam("sortBy") sortBy: Set<String> = setOf("id"),
                                   @RequestParam("sortDirection") sortDirection: Sort.Direction = Sort.Direction.ASC
    ): TransferTransactionListResponseDto
}