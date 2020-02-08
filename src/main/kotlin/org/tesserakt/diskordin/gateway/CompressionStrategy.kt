package org.tesserakt.diskordin.gateway

sealed class CompressionStrategy {
    object CompressAll : CompressionStrategy()
    data class CompressOnly(val shardIndexes: List<Int>) : CompressionStrategy()
}