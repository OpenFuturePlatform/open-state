//package finance.veles.blockchain
//
//import finance.veles.blockchain.model.Blockchain
//import finance.veles.blockchain.model.BlockchainTokenTransaction
//import finance.veles.blockchain.model.BlockchainTransaction
//import finance.veles.blockchain.model.BlockchainWallet
//import finance.veles.exception.BlockIsNotReadyException
//import finance.veles.property.BlockchainPaymentProperties
//import finance.veles.util.infiniteIterator
//import org.springframework.stereotype.Component
//import org.web3j.abi.FunctionReturnDecoder
//import org.web3j.abi.TypeReference
//import org.web3j.abi.Utils
//import org.web3j.abi.datatypes.Address
//import org.web3j.abi.datatypes.generated.Uint256
//import org.web3j.crypto.Credentials
//import org.web3j.crypto.Keys
//import org.web3j.protocol.Web3j
//import org.web3j.protocol.core.DefaultBlockParameterNumber
//import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject
//import org.web3j.protocol.http.HttpService
//import java.math.BigInteger
//
//
//@Component
//class BinanceSmartChainApi(properties: BlockchainPaymentProperties) : BlockchainApi {
//
//    private val web3j = properties.blockchain[Blockchain.BINANCE_SMART_CHAIN]!!.nodes
//        .map { Web3j.build(HttpService(it)) }
//        .infiniteIterator()
//
//
//    override fun generateWallet(): BlockchainWallet {
//        val keyPair = Keys.createEcKeyPair()
//        val credentials = Credentials.create(keyPair)
//        val privateKey = keyPair.privateKey.toString(16)
//
//        return BlockchainWallet(credentials.address, privateKey)
//    }
//
//    override fun getLatestBlockNumber(): Long = web3j.next().ethBlockNumber().send().blockNumber.toLong()
//
//    override fun getTransferTransactions(blockNumber: Long): List<BlockchainTransaction> {
//        val transactions = web3j.next().ethGetBlockByNumber(DefaultBlockParameterNumber(blockNumber), true).send()
//            ?.block
//            ?.transactions
//            ?.map { it.get() as TransactionObject }
//            ?.filter { it.to != null } // drop smart contract creation transactions
//            ?: throw BlockIsNotReadyException()
//
//        val tokenTransfers = transactions
//            .filter { isBep20Transfer(it) }
//            .map { mapBep20Transaction(it) }
//
//        val nativeTransfers = transactions
//            .filter { isNativeTransfer(it) }
//            .map { BlockchainTransaction(it.from, it.to, it.hash, it.value) }
//
//        return tokenTransfers + nativeTransfers
//    }
//
//    private fun isNativeTransfer(tx: TransactionObject): Boolean = tx.input == "0x"
//
//    private fun isBep20Transfer(tx: TransactionObject): Boolean = tx.input.startsWith(TRANSFER_METHOD_SIGNATURE)
//            && tx.input.length >= TRANSFER_INPUT_LENGTH
//
//    private fun mapBep20Transaction(tx: TransactionObject): BlockchainTransaction {
//        val result = FunctionReturnDecoder.decode(tx.input.drop(TRANSFER_METHOD_SIGNATURE.length), DECODE_TYPES)
//
//        return BlockchainTokenTransaction(
//            tx.from,
//            result[0].value as String,
//            tx.hash,
//            result[1].value as BigInteger,
//            tx.to
//        )
//    }
//
//    override fun type(): Blockchain = Blockchain.BINANCE_SMART_CHAIN
//
//    override fun isTransactionSuccessful(hash: String): Boolean = web3j.next().ethGetTransactionReceipt(hash).send()
//        .transactionReceipt
//        .get()
//        .isStatusOK
//
//    companion object {
//        private val DECODE_TYPES = Utils.convert(
//            listOf(
//                object : TypeReference<Address>(true) {},
//                object : TypeReference<Uint256>() {}
//            )
//        )
//
//        private const val TRANSFER_METHOD_SIGNATURE = "0xa9059cbb"
//        private const val TRANSFER_INPUT_LENGTH = 138
//        // TODO: Support method `transferFrom`
//    }
//
//}
