package org.tesserakt.diskordin.gateway.shard

typealias ShardIndex = Int
typealias LargeThreshold = Int

data class ShardThreshold(val overrides: Map<ShardIndex, LargeThreshold>)