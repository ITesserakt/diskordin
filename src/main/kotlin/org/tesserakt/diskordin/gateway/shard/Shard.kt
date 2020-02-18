package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.gateway.GatewayConnection

data class Shard(
    val token: String,
    val shardData: ShardData,
    val connection: GatewayConnection
) {
    data class ShardData(val current: Int, val total: Int)

    lateinit var sessionId: String internal set
    var sequence: Int? = null
        internal set
}