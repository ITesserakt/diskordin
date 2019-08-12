package ru.tesserakt.diskordin.core.entity

import ru.tesserakt.diskordin.core.data.Snowflake

interface IConnection : IEntity, INamed {
    val type: String
    val isRevoked: Boolean
    val integrations: Array<Pair<Snowflake, String>>
    val isVerified: Boolean
    val isFriendSyncing: Boolean
    val isShowingActivity: Boolean
    val visibility: Visibility

    enum class Visibility {
        None,
        Everyone;
    }
}