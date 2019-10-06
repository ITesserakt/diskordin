@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.core.data.json.request.DMCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.GroupDMCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.UserEditRequest
import ru.tesserakt.diskordin.core.entity.query.Query
import ru.tesserakt.diskordin.rest.Routes

internal object UserResource {
    object General {
        suspend fun getCurrentUser() =
            Routes.getCurrentUser()
                .newRequest()
                .resolve()

        suspend fun getUser(userId: Long) =
            Routes.getUser(userId)
                .newRequest()
                .resolve()

        suspend fun editCurrentUser(request: UserEditRequest) =
            Routes.modifyCurrentUser()
                .newRequest()
                .resolve(request)
    }

    object Guilds {
        suspend fun getCurrentUserGuilds(query: Query) =
            Routes.getCurrentUserGuilds()
                .newRequest()
                .queryParams(query)
                .resolve()

        suspend fun leaveGuild(guildId: Long) =
            Routes.leaveGuild(guildId)
                .newRequest()
                .resolve()
    }

    object Channels {
        suspend fun getUserPrivateChannels() =
            Routes.getDMs()
                .newRequest()
                .resolve()

        suspend fun joinPrivateChannel(request: DMCreateRequest) =
            Routes.createDM()
                .newRequest()
                .resolve(request)

        @Deprecated("GameBridge SDK is deprecated for now", ReplaceWith("joinPrivateChannel"))
        suspend fun joinGroupDM(request: GroupDMCreateRequest) =
            Routes.createGroupDM()
                .newRequest()
                .resolve(request)
    }

    object Connections {
        suspend fun getCurrentUserConnections() =
            Routes.getConnections()
                .newRequest()
                .resolve()
    }
}