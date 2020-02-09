package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ActivityResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.`object`.IActivity
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
internal class Activity(raw: ActivityResponse) : IActivity {
    override var startPlaying: Instant? = null
        private set
    @ExperimentalTime
    override var duration: Duration? = null
        private set
    override var endPlaying: Instant? = null
        private set

    init {
        if (raw.timestamps != null) {
            val (start, end) = raw.timestamps
            if (start != null)
                startPlaying = Instant.ofEpochSecond(start)
            if (end != null)
                endPlaying = Instant.ofEpochSecond(end)
            if (start != null && end != null)
                duration = end.seconds - start.seconds
        }
    }

    override val name: String = raw.name
    override val type: IActivity.Type = IActivity.Type.values().first { it.ordinal == raw.type }
    override val streamUrl: String? = raw.url
    override val applicationId: Snowflake? = raw.applicationId
    override val details: String? = raw.details
    override val state: String? = raw.state
    override val emoji: IActivity.IEmoji? = raw.emoji?.unwrap()
    override val party: IActivity.IParty? = raw.party?.unwrap()
    override val assets: IActivity.IAssets? = raw.assets?.unwrap()
    override val secrets: IActivity.ISecrets? = raw.secrets?.unwrap()
    override val instanceOfGame: Boolean? = raw.instance
    override val flags: ValuedEnum<IActivity.Flags, Short>? =
        raw.flags?.let { ValuedEnum(it.toShort(), Short.integral()) }

    class Emoji(raw: ActivityResponse.EmojiResponse) : IActivity.IEmoji {
        override val name: String = raw.name
        override val id: Snowflake? = raw.id
        override val isAnimated: Boolean? = raw.animated

        override fun toString(): String {
            return "Emoji(name='$name', id=$id, isAnimated=$isAnimated)"
        }
    }

    class Party(raw: ActivityResponse.PartyResponse) : IActivity.IParty {
        override val id: String? = raw.id
        override val currentSize: Int? = raw.size.getOrNull(0)
        override val maxSize: Int? = raw.size.getOrNull(1)
        override fun toString(): String {
            return StringBuilder("Party(")
                .appendln("id=$id, ")
                .appendln("currentSize=$currentSize, ")
                .appendln("maxSize=$maxSize")
                .appendln(")")
                .toString()
        }
    }

    class Assets(raw: ActivityResponse.AssetsResponse) : IActivity.IAssets {
        override val largeImageHash: String? = raw.largeImage
        override val largeText: String? = raw.largeText
        override val smallImageHash: String? = raw.smallImage
        override val smallText: String? = raw.smallText
        override fun toString(): String {
            return StringBuilder("Assets(")
                .appendln("largeImageHash=$largeImageHash, ")
                .appendln("largeText=$largeText, ")
                .appendln("smallImageHash=$smallImageHash, ")
                .appendln("smallText=$smallText")
                .appendln(")")
                .toString()
        }
    }

    class Secrets(raw: ActivityResponse.SecretsResponse) : IActivity.ISecrets {
        override val join: String? = raw.join
        override val spectate: String? = raw.spectate
        override val match: String? = raw.match
        override fun toString(): String {
            return StringBuilder("Secrets(")
                .appendln("join=$join, ")
                .appendln("spectate=$spectate, ")
                .appendln("match=$match")
                .appendln(")")
                .toString()
        }
    }

    override fun toString(): String {
        return StringBuilder("Activity(")
            .appendln("startPlaying=$startPlaying, ")
            .appendln("duration=$duration, ")
            .appendln("endPlaying=$endPlaying, ")
            .appendln("name='$name', ")
            .appendln("type=$type, ")
            .appendln("streamUrl=$streamUrl, ")
            .appendln("applicationId=$applicationId, ")
            .appendln("details=$details, ")
            .appendln("state=$state, ")
            .appendln("emoji=$emoji")
            .appendln("party=$party, ")
            .appendln("assets=$assets, ")
            .appendln("secrets=$secrets, ")
            .appendln("instanceOfGame=$instanceOfGame, ")
            .appendln("flags=$flags")
            .appendln(")")
            .toString()
    }
}
