package io.openfuture.state.watcher

interface BlockProcessor {
    suspend fun processNext()
}
