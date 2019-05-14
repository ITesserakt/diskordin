package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface IMember : IUser, IGuildObject {
    val nickname: String?
    override val name: String
        get() = nickname ?: username

    @FlowPreview
    val roles: Flow<IRole>

    val joinTime: Instant
}