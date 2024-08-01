//package io.openfuture.state.blockchain.contracts
//
//import io.reactivex.Flowable
//import io.reactivex.functions.Function
//import org.web3j.abi.EventEncoder
//import org.web3j.abi.TypeReference
//import org.web3j.abi.datatypes.Address
//import org.web3j.abi.datatypes.Event
//import org.web3j.abi.datatypes.Type
//import org.web3j.abi.datatypes.Utf8String
//import org.web3j.abi.datatypes.generated.Uint256
//import org.web3j.abi.datatypes.generated.Uint8
//import org.web3j.crypto.Credentials
//import org.web3j.protocol.Web3j
//import org.web3j.protocol.core.DefaultBlockParameter
//import org.web3j.protocol.core.RemoteCall
//import org.web3j.protocol.core.methods.request.EthFilter
//import org.web3j.protocol.core.methods.response.Log
//import org.web3j.protocol.core.methods.response.TransactionReceipt
//import org.web3j.tx.Contract
//import org.web3j.tx.Contract.EventValuesWithLog
//import org.web3j.tx.TransactionManager
//import org.web3j.tx.gas.ContractGasProvider
//import java.math.BigInteger
//import java.util.*
//
//class ERC20 {
//}
//
//class ERC20(
//
//): Contract() {
//    val BINARY = "Bin file was not provided"
//
//    val FUNC_NAME = "name"
//
//    val FUNC_APPROVE = "approve"
//
//    val FUNC_TOTALSUPPLY = "totalSupply"
//
//    val FUNC_TRANSFERFROM = "transferFrom"
//
//    val FUNC_DECIMALS = "decimals"
//
//    val FUNC_BALANCEOF = "balanceOf"
//
//    val FUNC_SYMBOL = "symbol"
//
//    val FUNC_TRANSFER = "transfer"
//
//    val FUNC_ALLOWANCE = "allowance"
//
//    val TRANSFER_EVENT = Event(
//        "Transfer",
//        Arrays.asList<TypeReference<*>>(
//            object : TypeReference<Address?>(true) {},
//            object : TypeReference<Address?>(true) {},
//            object : TypeReference<Uint256?>() {})
//    )
//    ;
//
//    val APPROVAL_EVENT = Event(
//        "Approval",
//        Arrays.asList<TypeReference<*>>(
//            object : TypeReference<Address?>(true) {},
//            object : TypeReference<Address?>(true) {},
//            object : TypeReference<Uint256?>() {})
//    )
//    ;
//
//    @Deprecated("")
//    fun ERC20(
//        contractAddress: String?,
//        web3j: Web3j?,
//        credentials: Credentials?,
//        gasPrice: BigInteger?,
//        gasLimit: BigInteger?
//    ) {
//        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit)
//    }
//
//    protected fun ERC20(
//        contractAddress: String?,
//        web3j: Web3j?,
//        credentials: Credentials?,
//        contractGasProvider: ContractGasProvider?
//    ) {
//        super(BINARY, contractAddress, web3j, credentials, contractGasProvider)
//    }
//
//    @Deprecated("")
//    protected fun ERC20(
//        contractAddress: String?,
//        web3j: Web3j?,
//        transactionManager: TransactionManager?,
//        gasPrice: BigInteger?,
//        gasLimit: BigInteger?
//    ) {
//        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit)
//    }
//
//    protected fun ERC20(
//        contractAddress: String?,
//        web3j: Web3j?,
//        transactionManager: TransactionManager?,
//        contractGasProvider: ContractGasProvider?
//    ) {
//        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider)
//    }
//
//    fun name(): RemoteCall<String> {
//        val function = org.web3j.abi.datatypes.Function(FUNC_NAME, listOf(),
//            java.util.List.of<TypeReference<*>>(object : TypeReference<Utf8String?>() {})
//        )
//        return executeRemoteCallSingleValueReturn<String>(function, String::class.java)
//    }
//
//    fun approve(_spender: String?, _value: BigInteger?): RemoteCall<TransactionReceipt> {
//        val function = org.web3j.abi.datatypes.Function(
//            FUNC_APPROVE,
//            Arrays.asList<Type<*>>(
//                Address(_spender),
//                Uint256(_value)
//            ), emptyList()
//        )
//        return executeRemoteCallTransaction(function)
//    }
//
//    fun totalSupply(): RemoteCall<BigInteger> {
//        val function = org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY, listOf(),
//            java.util.List.of<TypeReference<*>>(object : TypeReference<Uint256?>() {})
//        )
//        return executeRemoteCallSingleValueReturn<BigInteger>(function, BigInteger::class.java)
//    }
//
//    fun transferFrom(_from: String?, _to: String?, _value: BigInteger?): RemoteCall<TransactionReceipt> {
//        val function = org.web3j.abi.datatypes.Function(
//            FUNC_TRANSFERFROM,
//            Arrays.asList<Type<*>>(Address(_from), Address(_to), Uint256(_value)), emptyList()
//        )
//        return executeRemoteCallTransaction(function)
//    }
//
//    fun decimals(): RemoteCall<BigInteger> {
//        val function = org.web3j.abi.datatypes.Function(FUNC_DECIMALS, listOf(),
//            java.util.List.of<TypeReference<*>>(object : TypeReference<Uint8?>() {})
//        )
//        return executeRemoteCallSingleValueReturn<BigInteger>(function, BigInteger::class.java)
//    }
//
//    fun balanceOf(_owner: String?): RemoteCall<BigInteger> {
//        val function = org.web3j.abi.datatypes.Function(FUNC_BALANCEOF,
//            java.util.List.of<Type<*>>(Address(_owner)),
//            java.util.List.of<TypeReference<*>>(object : TypeReference<Uint256?>() {})
//        )
//        return executeRemoteCallSingleValueReturn<BigInteger>(function, BigInteger::class.java)
//    }
//
//    fun symbol(): RemoteCall<String> {
//        val function = org.web3j.abi.datatypes.Function(FUNC_SYMBOL, listOf(),
//            java.util.List.of<TypeReference<*>>(object : TypeReference<Utf8String?>() {})
//        )
//        return executeRemoteCallSingleValueReturn<String>(function, String::class.java)
//    }
//
//    fun transfer(_to: String?, _value: BigInteger?): RemoteCall<TransactionReceipt> {
//        val function = org.web3j.abi.datatypes.Function(
//            FUNC_TRANSFER,
//            Arrays.asList<Type<*>>(Address(_to), Uint256(_value)),
//            listOf<TypeReference<*>>(object : TypeReference<Uint256?>() {})
//        )
//        return executeRemoteCallTransaction(function)
//    }
//
//    fun allowance(_owner: String?, _spender: String?): RemoteCall<BigInteger> {
//        val function = org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE,
//            Arrays.asList<Type<*>>(Address(_owner), Address(_spender)),
//            java.util.List.of<TypeReference<*>>(object : TypeReference<Uint256?>() {})
//        )
//        return executeRemoteCallSingleValueReturn<BigInteger>(function, BigInteger::class.java)
//    }
//
//    fun getTransferEvents(transactionReceipt: TransactionReceipt?): List<TransferEventResponse> {
//        val valueList: List<EventValuesWithLog> = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt)
//        val responses: ArrayList<TransferEventResponse> = ArrayList<TransferEventResponse>(valueList.size)
//        for (eventValues in valueList) {
//            val typedResponse = TransferEventResponse()
//            typedResponse.log = eventValues.log
//            typedResponse._from = eventValues.indexedValues[0].value as String
//            typedResponse._to = eventValues.indexedValues[1].value as String
//            typedResponse._value = eventValues.nonIndexedValues[0].value as BigInteger
//            responses.add(typedResponse)
//        }
//        return responses
//    }
//
//    fun transferEventFlowable(filter: EthFilter?): Flowable<TransferEventResponse> {
//        return web3j.ethLogFlowable(filter).map(Function<Log, Any> { log ->
//            val eventValues: EventValuesWithLog = extractEventParametersWithLog(TRANSFER_EVENT, log)
//            val typedResponse = TransferEventResponse()
//            typedResponse.log = log
//            typedResponse._from = eventValues.indexedValues[0].value as String
//            typedResponse._to = eventValues.indexedValues[1].value as String
//            typedResponse._value = eventValues.nonIndexedValues[0].value as BigInteger
//            typedResponse
//        })
//    }
//
//    fun transferEventFlowable(
//        startBlock: DefaultBlockParameter?,
//        endBlock: DefaultBlockParameter?
//    ): Flowable<TransferEventResponse> {
//        val filter: EthFilter = EthFilter(startBlock, endBlock, getContractAddress())
//        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT))
//        return transferEventFlowable(filter)
//    }
//
//    fun getApprovalEvents(transactionReceipt: TransactionReceipt?): List<ApprovalEventResponse> {
//        val valueList: List<EventValuesWithLog> = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt)
//        val responses: ArrayList<ApprovalEventResponse> = ArrayList<ApprovalEventResponse>(valueList.size)
//        for (eventValues in valueList) {
//            val typedResponse = ApprovalEventResponse()
//            typedResponse.log = eventValues.log
//            typedResponse._owner = eventValues.indexedValues[0].value as String
//            typedResponse._spender = eventValues.indexedValues[1].value as String
//            typedResponse._value = eventValues.nonIndexedValues[0].value as BigInteger
//            responses.add(typedResponse)
//        }
//        return responses
//    }
//
//    fun approvalEventFlowable(filter: EthFilter?): Flowable<ApprovalEventResponse> {
//        return web3j.ethLogFlowable(filter).map(Function<Log, Any> { log ->
//            val eventValues: EventValuesWithLog = extractEventParametersWithLog(APPROVAL_EVENT, log)
//            val typedResponse = ApprovalEventResponse()
//            typedResponse.log = log
//            typedResponse._owner = eventValues.indexedValues[0].value as String
//            typedResponse._spender = eventValues.indexedValues[1].value as String
//            typedResponse._value = eventValues.nonIndexedValues[0].value as BigInteger
//            typedResponse
//        })
//    }
//
//    fun approvalEventFlowable(
//        startBlock: DefaultBlockParameter?,
//        endBlock: DefaultBlockParameter?
//    ): Flowable<ApprovalEventResponse> {
//        val filter: EthFilter = EthFilter(startBlock, endBlock, getContractAddress())
//        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT))
//        return approvalEventFlowable(filter)
//    }
//
//    @Deprecated("")
//    fun load(
//        contractAddress: String?,
//        web3j: Web3j?,
//        credentials: Credentials?,
//        gasPrice: BigInteger?,
//        gasLimit: BigInteger?
//    ): com.cryptoideas.sig.config.ERC20 {
//        return com.cryptoideas.sig.config.ERC20(contractAddress, web3j, credentials, gasPrice, gasLimit)
//    }
//
//    @Deprecated("")
//    fun load(
//        contractAddress: String?,
//        web3j: Web3j?,
//        transactionManager: TransactionManager?,
//        gasPrice: BigInteger?,
//        gasLimit: BigInteger?
//    ): com.cryptoideas.sig.config.ERC20 {
//        return com.cryptoideas.sig.config.ERC20(contractAddress, web3j, transactionManager, gasPrice, gasLimit)
//    }
//
//    fun load(
//        contractAddress: String?,
//        web3j: Web3j?,
//        credentials: Credentials?,
//        contractGasProvider: ContractGasProvider?
//    ): com.cryptoideas.sig.config.ERC20 {
//        return com.cryptoideas.sig.config.ERC20(contractAddress, web3j, credentials, contractGasProvider)
//    }
//
//    fun load(
//        contractAddress: String?,
//        web3j: Web3j?,
//        transactionManager: TransactionManager?,
//        contractGasProvider: ContractGasProvider?
//    ): com.cryptoideas.sig.config.ERC20 {
//        return com.cryptoideas.sig.config.ERC20(contractAddress, web3j, transactionManager, contractGasProvider)
//    }
//
//
//    class TransferEventResponse {
//        var log: Log? = null
//        var _from: String? = null
//        var _to: String? = null
//        var _value: BigInteger? = null
//    }
//
//
//    class ApprovalEventResponse {
//        var log: Log? = null
//        var _owner: String? = null
//        var _spender: String? = null
//        var _value: BigInteger? = null
//    }
