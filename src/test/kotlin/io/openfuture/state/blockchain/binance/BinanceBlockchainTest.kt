package io.openfuture.state.blockchain.binance

import com.binance.dex.api.client.BinanceDexApiNodeClient
import com.binance.dex.api.client.domain.BlockMeta
import com.binance.dex.api.client.domain.Infos
import com.binance.dex.api.client.domain.SyncInfo
import com.binance.dex.api.client.domain.TransferInfo
import com.binance.dex.api.client.domain.broadcast.Transaction
import com.binance.dex.api.client.domain.broadcast.TxType
import com.binance.dex.api.client.encoding.message.InputOutput
import com.binance.dex.api.client.encoding.message.Token
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.util.createDummyUnifiedBlock
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.util.*

class BinanceBlockchainTest {

    private val binanceClient: BinanceDexApiNodeClient = mock()

    private lateinit var binanceBlockchain: BinanceBlockchain

    @BeforeEach
    fun setUp() {
        binanceBlockchain = BinanceBlockchain(binanceClient)
    }

    @Test
    fun getLastBlockNumberShouldReturnProperValue() = runBlocking<Unit> {
        val nodeInfo = Infos()
        val syncInfo = SyncInfo()
        nodeInfo.syncInfo = syncInfo
        syncInfo.latestBlockHeight = 5

        given(binanceClient.nodeInfo).willReturn(nodeInfo)

        val result = binanceBlockchain.getLastBlockNumber()

        Assertions.assertThat(result).isEqualTo(5)
    }

    @Test
    fun getBlockShouldReturnProperValue() = runBlocking<Unit> {
        val block = createDummyUnifiedBlock()

        val blockMeta = BlockMeta()
        val header = BlockMeta.Header()

        header.time = Date.from(block.date.atZone(ZoneId.systemDefault()).toInstant());
        header.dataHash = block.hash
        blockMeta.header = header

        val input = InputOutput()
        input.address = block.transactions.first().from
        val output = InputOutput()
        output.address = block.transactions.first().to
        val coin = Token("BNB", block.transactions.first().amount.toLong())
        output.coins = listOf(coin)

        val transfer = TransferInfo()
        transfer.inputs = listOf(input)
        transfer.outputs = listOf(output)

        val transaction = Transaction()
        transaction.txType = TxType.TRANSFER
        transaction.realTx = transfer
        transaction.hash = block.transactions.first().hash

        given(binanceClient.getBlockMetaByHeight(1)).willReturn(blockMeta)
        given(binanceClient.getBlockTransactions(1)).willReturn(listOf(transaction))

        val result = binanceBlockchain.getBlock(1)

        Assertions.assertThat(result).isEqualTo(block)
    }

}
