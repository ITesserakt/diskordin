@file:Suppress("unused", "UNUSED_PARAMETER")

package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.util.AsyncStore
import ru.tesserakt.diskordin.util.Identified
import java.time.Duration

interface IGuild : IEntity, INamed, IDeletable {
    val iconHash: String?
    val splashHash: String?


    val owner: Identified<IMember>


    val afkChannel: AsyncStore<Snowflake?, IVoiceChannel?>
    val afkChannelTimeout: Duration

    val verificationLevel: VerificationLevel

    @FlowPreview
    val roles: Flow<IRole>
    @FlowPreview
    val channels: Flow<IGuildChannel>


    suspend fun findRole(id: Snowflake): IRole?

    @FlowPreview
    val members: Flow<IMember>
}

enum class VerificationLevel {
    None,
    Low,
    Medium,
    High,
    VeryHigh;

    companion object {
        fun of(value: Int) = when (value) {
            0 -> None
            1 -> Low
            2 -> Medium
            3 -> High
            4 -> VeryHigh
            else -> throw NoSuchElementException()
        }
    }
}