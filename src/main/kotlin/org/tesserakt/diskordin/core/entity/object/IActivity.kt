@file:Suppress("unused")

package org.tesserakt.diskordin.core.entity.`object`

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.typeclass.Integral
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
    val emoji: IEmoji?
    val party: IParty?
    val assets: IAssets?
    val secrets: ISecrets?
    val instanceOfGame: Boolean?
    val flags: ValuedEnum<Flags, Short>?

    enum class Flags(override val code: Short) : IValued<Flags, Short>, Integral<Short> by Short.integral() {
        INSTANCE(1 shl 0),
        JOIN(1 shl 1),
        SPECTATE(1 shl 2),
        JOIN_REQUEST(1 shl 3),
        SYNC(1 shl 4),
        PLAY(1 shl 5);
    }

    interface IEmoji : IDiscordObject {
        val name: String
        val id: Snowflake?
        val isAnimated: Boolean?
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
        Watching,
        Custom,
        Competing
    }
}
