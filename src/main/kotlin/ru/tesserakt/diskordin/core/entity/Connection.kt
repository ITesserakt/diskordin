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

    enum class Visibility(private val value: Int) {
        None(0),
        Everyone(1);

        companion object {
            fun of(value: Int) = values().first { it.value == value }
        }
    }
}