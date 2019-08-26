package io.openfuture.state.component

import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j

@Component
class Web3Wrapper(
        private val web3j: Web3j
) {

    fun getNetVersion(): String = web3j.netVersion().send().netVersion

}
