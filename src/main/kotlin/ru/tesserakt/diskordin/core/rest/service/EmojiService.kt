@file:Suppress("unused")

package ru.tesserakt.diskordin.core.rest.service

import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.rest.Routes

internal object EmojiService {
    object General {

        suspend fun getEmojis(guildId: Long) =
            Routes.getGuildEmojis(guildId)
                .newRequest()
                .resolve<Array<EmojiResponse>>()


        suspend fun getEmoji(guildId: Long, emojiId: Long) =
            Routes.getGuildEmoji(guildId, emojiId)
                .newRequest()
                .resolve<EmojiResponse>()


        suspend fun createEmoji(guildId: Long) =
            Routes.createGuildEmoji(guildId)
                .newRequest()
                .resolve<EmojiResponse>()


        suspend fun modifyEmoji(guildId: Long, emojiId: Long) =
            Routes.modifyGuildEmoji(guildId, emojiId)
                .newRequest()
                .resolve<EmojiResponse>()


        suspend fun deleteEmoji(guildId: Long, emojiId: Long) =
            Routes.deleteGuildEmoji(guildId, emojiId)
                .newRequest()
                .resolve<Unit>()
    }
}