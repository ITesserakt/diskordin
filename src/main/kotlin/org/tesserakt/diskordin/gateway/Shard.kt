package org.tesserakt.diskordin.gateway

private typealias Current = Int
private typealias Total = Int

data class Shard(
    val sequenceId: Int?,
    val token: String,
    val sessionId: String?,
    val shardData: Pair<Current, Total>
)