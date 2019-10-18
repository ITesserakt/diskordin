package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.`object`.IActivity
import ru.tesserakt.diskordin.impl.core.entity.`object`.Activity
import kotlin.time.ExperimentalTime

data class ActivityResponse(
    val name: String,
    val type: Int,
    val url: String? = null,
    val timestamps: TimestampsResponse? = null,
    val applicationId: Snowflake? = null,
    val details: String? = null,
    val state: String? = null,
    val party: PartyResponse? = null,
    val assets: AssetsResponse? = null,
    val secrets: SecretsResponse? = null,
    val instance: Boolean? = false,
    val flags: Long? = null
) : DiscordResponse<IActivity>() {
    @ExperimentalTime
    override fun unwrap(vararg params: Any): IActivity = Activity(this)

    data class TimestampsResponse(
        val start: Long? = null,
        val end: Long? = null
    )

    data class PartyResponse(
        val id: String = "",
        val size: Array<Int> = emptyArray()
    ) : DiscordResponse<IActivity.IParty>() {
        @ExperimentalTime
        override fun unwrap(vararg params: Any): IActivity.IParty = Activity.Party(this)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PartyResponse) return false

            if (id != other.id) return false
            if (!size.contentEquals(other.size)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + size.contentHashCode()
            return result
        }
    }

    data class AssetsResponse(
        val largeImage: String? = null,
        val largeText: String? = null,
        val smallImage: String? = null,
        val smallText: String? = null
    ) : DiscordResponse<IActivity.IAssets>() {
        @ExperimentalTime
        override fun unwrap(vararg params: Any): IActivity.IAssets = Activity.Assets(this)
    }

    data class SecretsResponse(
        val join: String? = null,
        val spectate: String? = null,
        val match: String? = null
    ) : DiscordResponse<IActivity.ISecrets>() {
        @ExperimentalTime
        override fun unwrap(vararg params: Any): IActivity.ISecrets = Activity.Secrets(this)
    }
}