package ru.tesserakt.diskordin.gateway.json.commands

import com.google.gson.annotations.SerializedName
import ru.tesserakt.diskordin.gateway.json.IGatewayCommand

data class Identify(
    val token: String,
    val properties: ConnectionProperties,
    val compress: Boolean = false,
    val largeThreshold: Int = 50,
    val shard: Array<Int> = emptyArray(),
    val guildSubscriptions: Boolean = true
) : IGatewayCommand {
    data class ConnectionProperties(
        @SerializedName("\$os") val os: String,
        @SerializedName("\$browser") val browser: String,
        @SerializedName("\$device") val device: String
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Identify) return false

        if (token != other.token) return false
        if (properties != other.properties) return false
        if (compress != other.compress) return false
        if (largeThreshold != other.largeThreshold) return false
        if (!shard.contentEquals(other.shard)) return false
        if (guildSubscriptions != other.guildSubscriptions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + compress.hashCode()
        result = 31 * result + largeThreshold
        result = 31 * result + shard.contentHashCode()
        result = 31 * result + guildSubscriptions.hashCode()
        return result
    }
}