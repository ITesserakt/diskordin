package ru.tesserakt.diskordin.impl.core.entity.`object`

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.ActivityResponse
import ru.tesserakt.diskordin.core.entity.`object`.IActivity
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant
import kotlin.contracts.ExperimentalContracts
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class Activity(raw: ActivityResponse) : IActivity {
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
    override val type: IActivity.Type? = IActivity.Type.values().find { it.ordinal == raw.type } ?: {
        println(raw.type)
        null
    }()
    @ExperimentalContracts
    override val streamUrl: String? = raw.url
    override val applicationId: Snowflake? = raw.applicationId
    override val details: String? = raw.details
    override val state: String? = raw.state
    override val party: IActivity.IParty? = raw.party?.unwrap()
    override val assets: IActivity.IAssets? = raw.assets?.unwrap()
    override val secrets: IActivity.ISecrets? = raw.secrets?.unwrap()
    override val instanceOfGame: Boolean? = raw.instance
    override val flags: ValuedEnum<IActivity.Flags>? = raw.flags?.let { ValuedEnum(it) }

    class Party(raw: ActivityResponse.PartyResponse) : IActivity.IParty {
        override val id: String? = raw.id
        override val currentSize: Int? = raw.size.getOrNull(0)
        override val maxSize: Int? = raw.size.getOrNull(1)
    }

    class Assets(raw: ActivityResponse.AssetsResponse) : IActivity.IAssets {
        override val largeImageHash: String? = raw.largeImage
        override val largeText: String? = raw.largeText
        override val smallImageHash: String? = raw.smallImage
        override val smallText: String? = raw.smallText
    }

    class Secrets(raw: ActivityResponse.SecretsResponse) : IActivity.ISecrets {
        override val join: String? = raw.join
        override val spectate: String? = raw.spectate
        override val match: String? = raw.match
    }
}
