package org.tesserakt.diskordin.gateway

sealed class GuildSubscriptionsStrategy {
    object SubscribeToAll : GuildSubscriptionsStrategy()
    data class SubscribeTo(val shardIndexes: List<Int>) : GuildSubscriptionsStrategy()
}