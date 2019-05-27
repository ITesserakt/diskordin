@file:Suppress("unused")

package ru.tesserakt.diskordin.core.rest.service

import ru.tesserakt.diskordin.core.data.json.request.DMCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.GroupDMCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.UserEditRequest
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.ConnectionResponse
import ru.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.rest.Routes

internal object UserService {
    object General {
        suspend fun getCurrentUser() =
            Routes.getCurrentUser()
                .newRequest()
                .resolve<UserResponse>()

        suspend fun getUser(userId: Long) =
            Routes.getUser(userId)
                .newRequest()
                .resolve<UserResponse>()

        suspend fun editCurrentUser(request: UserEditRequest) =
            Routes.modifyCurrentUser()
                .newRequest()
                .resolve<UserResponse>(request)
    }

    object Guilds {
        suspend fun getCurrentUserGuilds(query: Array<out Pair<String, Long>>) =
            Routes.getCurrentUserGuilds()
                .newRequest()
                .queryParams(*query)
                .resolve<Array<UserGuildResponse>>()

        suspend fun leaveGuild(guildId: Long) =
            Routes.leaveGuild(guildId)
                .newRequest()
                .resolve<Unit>()
    }

    object Channels {
        suspend fun getUserPrivateChannels() =
            Routes.getDMs()
                .newRequest()
                .resolve<Array<ChannelResponse>>()

        suspend fun joinPrivateChannel(request: DMCreateRequest) =
            Routes.createDM()
                .newRequest()
                .resolve<ChannelResponse>(request)

        @Deprecated("GameBridge SDK is deprecated for now", ReplaceWith("joinPrivateChannel"))
        suspend fun joinGroupDM(request: GroupDMCreateRequest) =
            Routes.createGroupDM()
                .newRequest()
                .resolve<ChannelResponse>(request)
    }

    object Connections {
        suspend fun getCurrentUserConnections() =
            Routes.getConnections()
                .newRequest()
                .resolve<Array<ConnectionResponse>>()
    }
}