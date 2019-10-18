@file:Suppress("unused")

package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.util.enums.IValued
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface IActivity : IDiscordObject {
    val name: String
    val type: Type?
    val streamUrl: String?
    val startPlaying: Instant?
    @ExperimentalTime
    val duration: Duration?
    val endPlaying: Instant?
    val applicationId: Snowflake?
    val details: String?
    val state: String?
    val party: IParty?
    val assets: IAssets?
    val secrets: ISecrets?
    val instanceOfGame: Boolean?
    val flags: ValuedEnum<Flags>?

    enum class Flags(override val value: Long) : IValued<Flags> {
        INSTANCE(1 shl 0),
        JOIN(1 shl 1),
        SPECTATE(1 shl 2),
        JOIN_REQUEST(1 shl 3),
        SYNC(1 shl 4),
        PLAY(1 shl 5);
    }

    interface ISecrets : IDiscordObject {
        val join: String?
        val spectate: String?
        val match: String?
    }

    interface IAssets : IDiscordObject {
        val largeImageHash: String?
        val largeText: String?
        val smallImageHash: String?
        val smallText: String?
    }

    interface IParty : IDiscordObject {
        val id: String?
        val currentSize: Int?
        val maxSize: Int?
    }

    enum class Type {
        Game,
        Streaming,
        Listening,
        Watching;
    }
}
