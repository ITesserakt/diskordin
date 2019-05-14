@file:Suppress("unused")

package ru.tesserakt.diskordin.core.rest.service

import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.core.data.json.response.*
import ru.tesserakt.diskordin.core.rest.Routes
import ru.tesserakt.diskordin.util.append

internal object GuildService {
    object General {

        suspend fun createGuild(request: GuildCreateRequest) =
            Routes.createGuild()
                .newRequest()
                .resolve<GuildResponse>(request)


        suspend fun getGuild(guildId: Long) =
            Routes.getGuild(guildId)
                .newRequest()
                .resolve<GuildResponse>()


        suspend fun modifyGuild(guildId: Long, request: GuildEditRequest, reason: String?) =
            Routes.modifyGuild(guildId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<GuildResponse>(request)


        suspend fun deleteGuild(guildId: Long) =
            Routes.deleteGuild(guildId)
                .newRequest()
                .resolve<Unit>()


        suspend fun modifyOwnNickname(guildId: Long, request: NicknameEditRequest) =
            Routes.modifyOwnNickname(guildId)
                .newRequest()
                .resolve<NicknameModifyResponse>(request)


        suspend fun getInvites(guildId: Long) =
            Routes.getInvites(guildId)
                .newRequest()
                .resolve<Array<InviteResponse>>()
    }

    object Channels {

        suspend fun getGuildChannels(guildId: Long) =
            Routes.getGuildChannels(guildId)
                .newRequest()
                .resolve<Array<ChannelResponse>>()


        suspend fun createGuildChannel(guildId: Long, request: ChannelCreateRequest, reason: String?) =
            Routes.createGuildChannel(guildId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<ChannelResponse>(request)


        suspend fun modifyGuildChannelPositions(guildId: Long, request: Array<PositionEditRequest>) =
            Routes.modifyGuildChannelPositions(guildId)
                .newRequest()
                .resolve<Unit>(request)
    }

    object Members {

        suspend fun getMember(guildId: Long, userId: Long) =
            Routes.getMember(guildId, userId)
                .newRequest()
                .resolve<GuildMemberResponse>()


        suspend fun getMembers(guildId: Long, query: Array<out Pair<String, Long>>) =
            Routes.getMembers(guildId)
                .newRequest()
                .queryParams(*query)
                .resolve<Array<GuildMemberResponse>>()


        suspend fun newMember(guildId: Long, userId: Long, request: MemberAddRequest) =
            Routes.addMember(guildId, userId)
                .newRequest()
                .resolve<GuildMemberResponse>(request)


        suspend fun modifyMember(guildId: Long, userId: Long, request: MemberEditRequest, reason: String?) =
            Routes.modifyMember(guildId, userId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>(request)


        suspend fun removeMember(guildId: Long, userId: Long, reason: String?) =
            Routes.removeMember(guildId, userId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()
    }

    object Roles {

        suspend fun addMemberRole(guildId: Long, userId: Long, roleId: Long, reason: String?) =
            Routes.addMemberRole(guildId, userId, roleId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()


        suspend fun deleteMemberRole(guildId: Long, userId: Long, roleId: Long, reason: String?) =
            Routes.removeMemberRole(guildId, userId, roleId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()


        suspend fun getRoles(guildId: Long) =
            Routes.getRoles(guildId)
                .newRequest()
                .resolve<Array<RoleResponse>>()


        suspend fun createRole(guildId: Long, request: GuildRoleCreateRequest, reason: String?) =
            Routes.createRole(guildId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<RoleResponse>(request)


        suspend fun editRolePositions(guildId: Long, request: Array<PositionEditRequest>) =
            Routes.modifyRolePositions(guildId)
                .newRequest()
                .resolve<Array<RoleResponse>>(request)


        suspend fun editRole(guildId: Long, roleId: Long, request: GuildRoleEditRequest, reason: String?) =
            Routes.modifyRole(guildId, roleId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<RoleResponse>(request)


        suspend fun deleteRole(guildId: Long, roleId: Long, reason: String?) =
            Routes.deleteRole(guildId, roleId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()
    }

    object Bans {

        suspend fun getBans(guildId: Long) =
            Routes.getBans(guildId)
                .newRequest()
                .resolve<Array<BanResponse>>()


        suspend fun getBan(guildId: Long, userId: Long) =
            Routes.getBan(guildId, userId)
                .newRequest()
                .resolve<BanResponse>()


        suspend fun createBan(guildId: Long, userId: Long, query: Array<out Pair<String, Long>>, reason: String?) =
            Routes.createBan(guildId, userId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.queryParams(*query)
                .resolve<Unit>()


        suspend fun removeBan(guildId: Long, userId: Long, reason: String?) =
            Routes.removeBan(guildId, userId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()
    }

    object Prunes {

        suspend fun getPruneCount(guildId: Long, query: Array<out Pair<String, Long>>) =
            Routes.getPruneCount(guildId)
                .newRequest()
                .queryParams(*query)
                .resolve<PruneResponse>()


        suspend fun startPrune(guildId: Long, query: Array<out Pair<String, Long>>, reason: String?) =
            Routes.beginPrune(guildId)
                .newRequest()
                .queryParams(*query)
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<PruneResponse>()
    }

    object Integrations {

        suspend fun getIntegrations(guildId: Long) =
            Routes.getIntegrations(guildId)
                .newRequest()
                .resolve<Array<GuildIntegrationResponse>>()


        suspend fun createIntegration(guildId: Long, request: IntegrationCreateRequest) =
            Routes.createIntegration(guildId)
                .newRequest()
                .resolve<Unit>(request)


        suspend fun editIntegration(guildId: Long, integrationId: Long, request: IntegrationEditRequest) =
            Routes.modifyIntegration(guildId, integrationId)
                .newRequest()
                .resolve<Unit>(request)


        suspend fun deleteIntegration(guildId: Long, integrationId: Long) =
            Routes.deleteIntegration(guildId, integrationId)
                .newRequest()
                .resolve<Unit>()


        suspend fun syncIntegration(guildId: Long, integrationId: Long) =
            Routes.syncIntegration(guildId, integrationId)
                .newRequest()
                .resolve<Unit>()
    }

    object Embeds {

        suspend fun getEmbed(guildId: Long) =
            Routes.getEmbed(guildId)
                .newRequest()
                .resolve<GuildEmbedResponse>()


        suspend fun editEmbed(guildId: Long, request: GuildEmbedEditRequest) =
            Routes.getEmbed(guildId)
                .newRequest()
                .resolve<GuildEmbedResponse>(request)
    }
}