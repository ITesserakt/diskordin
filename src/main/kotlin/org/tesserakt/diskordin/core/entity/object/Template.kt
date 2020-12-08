package org.tesserakt.diskordin.core.entity.`object`

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.INamed
import org.tesserakt.diskordin.core.entity.IUser

interface ITemplate : INamed {
    val code: String
    val description: String?
    val usageCount: Int
    val creator: Identified<IUser>
    val createdAt: Instant
    val updatedAt: Instant
    val sourceGuild: Identified<IGuild>
    val isSynced: Boolean

    suspend fun unpackTemplate(): IGuild
}