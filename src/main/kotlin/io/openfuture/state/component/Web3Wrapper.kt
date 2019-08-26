package io.openfuture.state.component

import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import java.math.BigInteger

@Component
class Web3Wrapper(
        private val web3j: Web3j
) {

    fun getNetVersion(): String = web3j.netVersion().send().netVersion

    fun getEthBalance(address: String): BigInteger? = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
            .sendAsync()
            .get()
            .balance

}
