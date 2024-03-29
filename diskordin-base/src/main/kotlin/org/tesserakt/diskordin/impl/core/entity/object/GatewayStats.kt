package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.json.response.GatewayBotResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal class GatewayStats(raw: GatewayBotResponse) : IGatewayStats {
    override val url: String = raw.url
    override val shards: Int = raw.shards
    override val session: IGatewayStats.ISession = raw.sessionMeta.unwrap()

    class Session(raw: GatewayBotResponse.SessionStartLimit) : IGatewayStats.ISession {
        override val total: Int = raw.total
        override val remaining: Int = raw.remaining

        @ExperimentalTime
        override val resetAfter: Duration = Duration.milliseconds(raw.resetAfter)

        @ExperimentalTime
        override fun toString(): String {
            return "Session(total=$total, remaining=$remaining, resetAfter=$resetAfter)"
        }
    }

    override fun toString(): String {
        return "GatewayStats(url='$url', shards=$shards, session=$session)"
    }
}