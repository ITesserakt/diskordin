package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IEmoji

interface IReaction : IDiscordObject {
    val count: Int
    val selfReacted: Boolean
    val emoji: IEmoji
}