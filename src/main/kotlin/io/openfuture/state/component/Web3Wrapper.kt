package io.openfuture.state.component

import io.openfuture.state.controller.domain.dto.TransactionDto
import io.openfuture.state.service.StateTrackingService
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@Component
class Web3Wrapper(
        private val web3j: Web3j,
        private val stateService: StateTrackingService
) {

    private var blockSubscriber: Disposable? = null


    fun getEthBalance(address: String): Long = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
            .sendAsync()
            .get()
            .balance
            .toLong()

//    @PostConstruct
    fun subscribe() {
        blockSubscriber = web3j.blockFlowable(true).subscribe(
                Consumer { processBlock(it.block) },
                onErrorSubscription()
        )
    }

//    @PreDestroy
    fun destroy() {
        unsubscribe()
    }

    private fun unsubscribe() {
        blockSubscriber?.dispose()
    }

    private fun processBlock(block: EthBlock.Block?) {
        if(block == null) return

        val transactions = block.transactions

        transactions.forEach { txObject ->
            val tx = txObject.get() as EthBlock.TransactionObject

            if ((tx.from != null && stateService.isTrackedAddress(tx.from, 1)) ||
                    (tx.to != null && stateService.isTrackedAddress(tx.to, 1))) {
                stateService.processTransaction(getTransaction(tx, block.timestamp))
            }
        }
    }

    private fun onErrorSubscription(): Consumer<Throwable> = Consumer {
        log.warn("Error subscription: $it")
        subscribe()
    }

    private fun getTransaction(tx: EthBlock.TransactionObject, timestamp: BigInteger): TransactionDto =
            TransactionDto(1, tx.hash, tx.from, tx.to, tx.value.toLong(), timestamp.toLong(), tx.blockNumber.toLong(), tx.blockHash)


    companion object {
        private val log = LoggerFactory.getLogger(Web3Wrapper::class.java)
    }

}
