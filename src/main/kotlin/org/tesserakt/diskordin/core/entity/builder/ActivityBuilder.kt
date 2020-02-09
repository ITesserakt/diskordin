package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ActivityResponse
import org.tesserakt.diskordin.core.entity.`object`.IActivity
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant

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
    private var isGameInstance: Boolean = false
    private var flags: ValuedEnum<IActivity.Flags, Short>? = null

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

    inline fun ActivityBuilder.url(value: String) = URL(value)
    inline fun ActivityBuilder.timestamps(builder: TimestampBuilder.() -> Unit) = TimestampBuilder().apply(builder)
    inline fun ActivityBuilder.application(id: Snowflake) = id
    inline fun ActivityBuilder.details(value: String) = value
    inline fun ActivityBuilder.state(value: String) = State(value)
    inline fun ActivityBuilder.flags(vararg flag: IActivity.Flags) = flag

    @RequestBuilder
    class TimestampBuilder {
        private var start: Long? = null
        private var end: Long? = null

        operator fun Start.unaryPlus() {
            start = this.v
        }

        operator fun Instant.unaryPlus() {
            end = this.toEpochMilli()
        }

        inline fun TimestampBuilder.start(value: Instant) = Start(value.toEpochMilli())
        inline fun TimestampBuilder.end(value: Instant) = value

        internal fun create() = ActivityResponse.TimestampsResponse(
            start, end
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
        assets,
        secrets,
        isGameInstance,
        flags?.code?.toLong()
    )
}