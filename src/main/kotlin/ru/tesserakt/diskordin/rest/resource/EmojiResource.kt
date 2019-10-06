@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import ru.tesserakt.diskordin.rest.Routes

internal object EmojiResource {
    object General {

        suspend fun getEmojis(guildId: Long) =
            Routes.getGuildEmojis(guildId)
                .newRequest()
                .resolve()


        suspend fun getEmoji(guildId: Long, emojiId: Long) =
            Routes.getGuildEmoji(guildId, emojiId)
                .newRequest()
                .resolve()


        suspend fun createEmoji(guildId: Long, request: EmojiCreateRequest) =
            Routes.createGuildEmoji(guildId)
                .newRequest()
                .resolve(request)


        suspend fun modifyEmoji(
            guildId: Long,
            emojiId: Long,
            request: EmojiEditRequest
        ) = Routes.modifyGuildEmoji(guildId, emojiId)
            .newRequest()
            .resolve(request)


        suspend fun deleteEmoji(guildId: Long, emojiId: Long) =
            Routes.deleteGuildEmoji(guildId, emojiId)
                .newRequest()
                .resolve()
    }
}