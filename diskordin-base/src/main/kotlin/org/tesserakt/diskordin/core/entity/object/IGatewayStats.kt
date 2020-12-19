package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.entity.IDiscordObject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface IGatewayStats : IDiscordObject {
    val url: String
    val shards: Int
    val session: ISession

    interface ISession : IDiscordObject {
        val total: Int
        val remaining: Int
        @ExperimentalTime
        val resetAfter: Duration
    }
}