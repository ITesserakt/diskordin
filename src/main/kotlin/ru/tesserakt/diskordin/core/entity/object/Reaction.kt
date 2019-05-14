package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IEmoji

interface IReaction : IDiscordObject {
    val count: Int
    val selfReacted: Boolean
    val emoji: IEmoji
}