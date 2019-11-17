package org.tesserakt.diskordin.gateway.json.events

import com.google.gson.annotations.SerializedName
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class Hello constructor(
    val heartbeatInterval: Long,
    @SerializedName("_trace") val trace: Array<String>
) : IRawEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hello) return false

        if (heartbeatInterval != other.heartbeatInterval) return false
        if (!trace.contentEquals(other.trace)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = heartbeatInterval.hashCode()
        result = 31 * result + trace.contentHashCode()
        return result
    }
}