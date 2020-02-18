package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.gateway.GatewayConnection

private typealias Current = Int
private typealias Total = Int

data class Shard(
    val token: String,
    val shardData: Pair<Current, Total>,
    val connection: GatewayConnection
) {
    lateinit var sessionId: String internal set
    var sequence: Int? = null
        internal set
}