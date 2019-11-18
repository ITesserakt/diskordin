package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.json.response.GatewayBotResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class GatewayStats(raw: GatewayBotResponse) : IGatewayStats {
    override val url: String = raw.url
    override val shards: Int = raw.shards
    override val session: IGatewayStats.ISession = raw.sessionMeta.unwrap()

    class Session(raw: GatewayBotResponse.SessionStartLimit) : IGatewayStats.ISession {
        override val total: Int = raw.total
        override val remaining: Int = raw.remaining
        @ExperimentalTime
        override val resetAfter: Duration = raw.resetAfter.milliseconds

        @ExperimentalTime
        override fun toString(): String {
            return StringBuilder("Session(")
                .appendln("total=$total, ")
                .appendln("remaining=$remaining, ")
                .appendln("resetAfter=$resetAfter")
                .appendln(")")
                .toString()
        }
    }

    override fun toString(): String {
        return StringBuilder("GatewayStats(")
            .appendln("url='$url', ")
            .appendln("shards=$shards, ")
            .appendln("session=$session")
            .appendln(")")
            .toString()
    }
}