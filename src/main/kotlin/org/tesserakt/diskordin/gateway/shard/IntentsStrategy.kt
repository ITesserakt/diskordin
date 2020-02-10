package org.tesserakt.diskordin.gateway.shard

sealed class IntentsStrategy {
    object EnableAll : IntentsStrategy()
    data class EnableOnly(val enabled: Map<ShardIndex, Short>) : IntentsStrategy()
}