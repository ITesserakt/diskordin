package org.tesserakt.diskordin.core.entity.builder

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ActivityResponse
import org.tesserakt.diskordin.core.entity.`object`.IActivity
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.enums.or

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class ActivityBuilder(
    private val name: String,
    private val type: IActivity.Type
) {
    private var url: String? = null
    private var timestamps: ActivityResponse.TimestampsResponse? = null
    private var applicationId: Snowflake? = null
    private var details: String? = null
    private var state: String? = null
    private var party: ActivityResponse.PartyResponse? = null
    private var assets: ActivityResponse.AssetsResponse? = null
    private var secrets: ActivityResponse.SecretsResponse? = null
    private val isGameInstance: Boolean?
        get() {
            return if (type == IActivity.Type.Game) true
            else null
        }
    private var flags: ValuedEnum<IActivity.Flags, Short>? = null
    private var emoji: ActivityResponse.EmojiResponse? = null

    operator fun URL.unaryPlus() {
        url = this.v
    }

    operator fun TimestampBuilder.unaryPlus() {
        this@ActivityBuilder.timestamps = this.create()
    }

    operator fun Snowflake.unaryPlus() {
        applicationId = this
    }

    operator fun String.unaryPlus() {
        details = this
    }

    operator fun State.unaryPlus() {
        state = this.v
    }

    operator fun Array<out IActivity.Flags>.unaryPlus() {
        this.forEach {
            flags = flags?.or(it)
        }
    }

    internal operator fun Name.unaryPlus() {
        emoji = ActivityResponse.EmojiResponse(v)
    }

    inline fun ActivityBuilder.url(value: String) = URL(value)
    inline fun ActivityBuilder.timestamps(builder: TimestampBuilder.() -> Unit) = TimestampBuilder().apply(builder)
    inline fun ActivityBuilder.application(id: Snowflake) = id
    inline fun ActivityBuilder.details(value: String) = value
    inline fun ActivityBuilder.state(value: String) = State(value)
    inline fun ActivityBuilder.flags(vararg flag: IActivity.Flags) = flag
    internal inline fun ActivityBuilder.emoji(name: String) = Name(name)

    @RequestBuilder
    class TimestampBuilder {
        private var start: Instant? = null
        private var end: Instant? = null

        operator fun Start.unaryPlus() {
            start = this.v
        }

        operator fun Instant.unaryPlus() {
            end = this
        }

        inline fun TimestampBuilder.start(value: Instant) = Start(value)
        inline fun TimestampBuilder.end(value: Instant) = value

        internal fun create() = ActivityResponse.TimestampsResponse(
            start?.toEpochMilliseconds(), end?.toEpochMilliseconds()
        )
    }

    internal fun create(): ActivityResponse = ActivityResponse(
        name,
        type.ordinal,
        url,
        timestamps,
        applicationId,
        details,
        state,
        party,
        emoji,
        assets,
        secrets,
        isGameInstance,
        flags?.code?.toLong()
    )
}