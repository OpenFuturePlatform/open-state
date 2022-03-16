package io.openfuture.state.controller.request

import io.openfuture.state.blockchain.dto.UnifiedBlock

data class ManualTransactionRequest(val blockchainName: String,
                                    val unifiedBlock: UnifiedBlock)
