package org.tesserakt.diskordin.core.entity

import org.tesserakt.diskordin.core.data.Snowflake

interface IConnection : IEntity, INamed {
    val type: String
    val isRevoked: Boolean
    val integrations: List<Pair<Snowflake, String>>
    val isVerified: Boolean
    val isFriendSyncing: Boolean
    val isShowingActivity: Boolean
    val visibility: Visibility

    enum class Visibility {
        None,
        Everyone;
    }
}