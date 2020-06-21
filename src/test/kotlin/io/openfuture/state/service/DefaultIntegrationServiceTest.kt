package io.openfuture.state.service

import io.openfuture.state.component.Web3Wrapper
import io.openfuture.state.openchain.component.openrpc.OpenChainWrapper
import io.openfuture.state.util.createDummyBlockchain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock

class DefaultIntegrationServiceTest {

    private val web3Wrapper: Web3Wrapper = mock(Web3Wrapper::class.java)
    private val openChainWrapper: OpenChainWrapper = mock(OpenChainWrapper::class.java)

    private lateinit var integrationService: IntegrationService

    @Before
    fun setUp() {
        integrationService = DefaultIntegrationService(web3Wrapper, openChainWrapper)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getBalance_WhenBlockchainTitleIsNotSupported_ShouldThrowIllegalArgumentException() {
        val blockchain = createDummyBlockchain()

        integrationService.getBalance("address", blockchain)
    }

    @Test
    fun getBalance_WhenBlockchainIsEthereum_ShouldReturnBalanceFromEthereum() {
        val address = "address"
        val blockchain = createDummyBlockchain(title = "Ethereum")

        given(web3Wrapper.getEthBalance(address)).willReturn(100L)
        given(openChainWrapper.getBalance(address)).willReturn(200L)

        val actual = integrationService.getBalance(address, blockchain)

        assertEquals(100L, actual)
    }

    @Test
    fun getBalance_WhenBlockchainIsOpenChain_ShouldReturnBalanceFromOpenChain() {
        val address = "address"
        val blockchain = createDummyBlockchain(title = "OPEN Chain")

        given(web3Wrapper.getEthBalance(address)).willReturn(100L)
        given(openChainWrapper.getBalance(address)).willReturn(200L)

        val actual = integrationService.getBalance(address, blockchain)

        assertEquals(200L, actual)
    }
}