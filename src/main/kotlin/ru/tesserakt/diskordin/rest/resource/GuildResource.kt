@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.core.entity.query.Query
import ru.tesserakt.diskordin.rest.Routes

internal object GuildResource {
    object General {

        suspend fun createGuild(request: GuildCreateRequest) =
            Routes.createGuild()
                .newRequest()
                .resolve(request)


        suspend fun getGuild(guildId: Long) =
            Routes.getGuild(guildId)
                .newRequest()
                .resolve()


        suspend fun modifyGuild(guildId: Long, request: GuildEditRequest, reason: String?) =
            Routes.modifyGuild(guildId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun deleteGuild(guildId: Long) =
            Routes.deleteGuild(guildId)
                .newRequest()
                .resolve()


        suspend fun modifyOwnNickname(guildId: Long, request: NicknameEditRequest) =
            Routes.modifyOwnNickname(guildId)
                .newRequest()
                .resolve(request)


        suspend fun getInvites(guildId: Long) =
            Routes.getInvites(guildId)
                .newRequest()
                .resolve()
    }

    object Channels {

        suspend fun getGuildChannels(guildId: Long) =
            Routes.getGuildChannels(guildId)
                .newRequest()
                .resolve()


        suspend fun createGuildChannel(guildId: Long, request: ChannelCreateRequest, reason: String?) =
            Routes.createGuildChannel(guildId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun modifyGuildChannelPositions(guildId: Long, request: Array<PositionEditRequest>) =
            Routes.modifyGuildChannelPositions(guildId)
                .newRequest()
                .resolve(request)
    }

    object Members {

        suspend fun getMember(guildId: Long, userId: Long) =
            Routes.getMember(guildId, userId)
                .newRequest()
                .resolve()


        suspend fun getMembers(guildId: Long, query: Query) =
            Routes.getMembers(guildId)
                .newRequest()
                .queryParams(query)
                .resolve()


        suspend fun newMember(guildId: Long, userId: Long, request: MemberAddRequest) =
            Routes.addMember(guildId, userId)
                .newRequest()
                .resolve(request)


        suspend fun modifyMember(guildId: Long, userId: Long, request: MemberEditRequest, reason: String?) =
            Routes.modifyMember(guildId, userId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun removeMember(guildId: Long, userId: Long, reason: String?) =
            Routes.removeMember(guildId, userId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()
    }

    object Roles {

        suspend fun addMemberRole(guildId: Long, userId: Long, roleId: Long, reason: String?) =
            Routes.addMemberRole(guildId, userId, roleId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()


        suspend fun deleteMemberRole(guildId: Long, userId: Long, roleId: Long, reason: String?) =
            Routes.removeMemberRole(guildId, userId, roleId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()


        suspend fun getRoles(guildId: Long) =
            Routes.getRoles(guildId)
                .newRequest()
                .resolve()


        suspend fun createRole(guildId: Long, request: GuildRoleCreateRequest, reason: String?) =
            Routes.createRole(guildId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun editRolePositions(guildId: Long, request: Array<PositionEditRequest>) =
            Routes.modifyRolePositions(guildId)
                .newRequest()
                .resolve(request)


        suspend fun editRole(guildId: Long, roleId: Long, request: GuildRoleEditRequest, reason: String?) =
            Routes.modifyRole(guildId, roleId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun deleteRole(guildId: Long, roleId: Long, reason: String?) =
            Routes.deleteRole(guildId, roleId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()
    }

    object Bans {

        suspend fun getBans(guildId: Long) =
            Routes.getBans(guildId)
                .newRequest()
                .resolve()


        suspend fun getBan(guildId: Long, userId: Long) =
            Routes.getBan(guildId, userId)
                .newRequest()
                .resolve()


        suspend fun createBan(guildId: Long, userId: Long, query: Query) =
            Routes.createBan(guildId, userId)
                .newRequest()
                .queryParams(query)
                .resolve()


        suspend fun removeBan(guildId: Long, userId: Long, reason: String?) =
            Routes.removeBan(guildId, userId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()
    }

    object Prunes {

        suspend fun getPruneCount(guildId: Long, query: Query) =
            Routes.getPruneCount(guildId)
                .newRequest()
                .queryParams(query)
                .resolve()


        suspend fun startPrune(guildId: Long, query: Query, reason: String?) =
            Routes.beginPrune(guildId)
                .newRequest()
                .queryParams(query)
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()
    }

    object Integrations {

        suspend fun getIntegrations(guildId: Long) =
            Routes.getIntegrations(guildId)
                .newRequest()
                .resolve()


        suspend fun createIntegration(guildId: Long, request: IntegrationCreateRequest) =
            Routes.createIntegration(guildId)
                .newRequest()
                .resolve(request)


        suspend fun editIntegration(guildId: Long, integrationId: Long, request: IntegrationEditRequest) =
            Routes.modifyIntegration(guildId, integrationId)
                .newRequest()
                .resolve(request)


        suspend fun deleteIntegration(guildId: Long, integrationId: Long) =
            Routes.deleteIntegration(guildId, integrationId)
                .newRequest()
                .resolve()


        suspend fun syncIntegration(guildId: Long, integrationId: Long) =
            Routes.syncIntegration(guildId, integrationId)
                .newRequest()
                .resolve()
    }

    object Embeds {

        suspend fun getEmbed(guildId: Long) =
            Routes.getEmbed(guildId)
                .newRequest()
                .resolve()


        suspend fun editEmbed(guildId: Long, request: GuildEmbedEditRequest) =
            Routes.getEmbed(guildId)
                .newRequest()
                .resolve(request)
    }
}