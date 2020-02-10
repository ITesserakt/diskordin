package org.tesserakt.diskordin.gateway.shard

sealed class CompressionStrategy {
    object CompressAll : CompressionStrategy()
    data class CompressOnly(val shardIndexes: List<Int>) : CompressionStrategy()
}