@file:Suppress("unused")

package ru.tesserakt.diskordin.rest

import ru.tesserakt.diskordin.core.data.json.response.*

internal object Routes {

//#############################################################################

    fun getChannel(channelId: Long) =
        Route.get<ChannelResponse>("/channels/$channelId")

    fun modifyChannel(channelId: Long) =
        Route.put<ChannelResponse>("/channels/$channelId")

    fun partialModifyChannel(channelId: Long) =
        Route.patch<ChannelResponse>("/channels/$channelId")

    fun closeChannel(channelId: Long) =
        Route.delete<ChannelResponse>("/channels/$channelId")

    fun getMessages(channelId: Long) =
        Route.get<Array<MessageResponse>>("/channels/$channelId/messages")

    fun getMessage(channelId: Long, messageId: Long) =
        Route.get<MessageResponse>("/channels/$channelId/messages/$messageId")

    fun createMessage(channelId: Long) =
        Route.post<MessageResponse>("/channels/$channelId/messages")

    fun createReaction(channelId: Long, messageId: Long, emoji: String) =
        Route.put<Unit>("/channels/$channelId/messages/$messageId/reactions/$emoji/@me")

    fun deleteOwnReaction(channelId: Long, messageId: Long, emoji: String) =
        Route.delete<Unit>("/channels/$channelId/messages/$messageId/reactions/$emoji/@me")

    fun deleteReaction(channelId: Long, messageId: Long, emoji: String, userId: Long) =
        Route.delete<Unit>("/channels/$channelId/messages/$messageId/reactions/$emoji/$userId")

    fun getReactions(channelId: Long, messageId: Long, emoji: String) =
        Route.get<Array<UserResponse>>("/channels/$channelId/messages/$messageId/reactions/$emoji")

    fun deleteAllReactions(channelId: Long, messageId: Long) =
        Route.delete<Unit>("/channels/$channelId/messages/$messageId/reactions")

    fun editMessage(channelId: Long, messageId: Long) =
        Route.patch<MessageResponse>("/channels/$channelId/messages/$messageId")

    fun deleteMessage(channelId: Long, messageId: Long) =
        Route.delete<Unit>("/channels/$channelId/messages/$messageId")

    fun bulkDeleteMessages(channelId: Long) =
        Route.post<Unit>("/channels$channelId/messages/bulk-delete")

    fun editChannelPermissions(channelId: Long, overwriteId: Long) =
        Route.put<Unit>("/channels/$channelId/permissions/$overwriteId")

    fun getChannelInvites(channelId: Long) =
        Route.get<Array<InviteResponse>>("/channels/$channelId/invites")

    fun createChannelInvite(channelId: Long) =
        Route.post<InviteResponse>("/channels/$channelId/invites")

    fun deleteChannelPermission(channelId: Long, overwriteId: Long) =
        Route.delete<Unit>("/channels/$channelId/permissions/$overwriteId")

    fun triggerTypingIndicator(channelId: Long) =
        Route.post<Unit>("/channels/$channelId/typing")

    fun getPinnedMessages(channelId: Long) =
        Route.get<Array<MessageResponse>>("/channels/$channelId/pins")

    fun addPinnedMessage(channelId: Long, messageId: Long) =
        Route.put<Unit>("/channels/$channelId/pins/$messageId")

    fun deletePinnedMessage(channelId: Long, messageId: Long) =
        Route.delete<Unit>("/channels/$channelId/pins/$messageId")

    fun addGroupDMRecipient(channelId: Long, userId: Long) =
        Route.put<Unit>("/channels/$channelId/recipients/$userId")

    fun deleteGroupDMRecipient(channelId: Long, userId: Long) =
        Route.delete<Unit>("/channels/$channelId/recipients/$userId")

//#############################################################################

    fun getGuildEmojis(guildId: Long) =
        Route.get<Array<EmojiResponse>>("/guilds/$guildId/emojis")

    fun getGuildEmoji(guildId: Long, emojiId: Long) =
        Route.get<EmojiResponse>("/guilds/$guildId/emojis/$emojiId")

    fun createGuildEmoji(guildId: Long) =
        Route.post<EmojiResponse>("/guilds/$guildId/emojis")

    fun modifyGuildEmoji(guildId: Long, emojiId: Long) =
        Route.patch<EmojiResponse>("/guilds/$guildId/emojis/$emojiId")

    fun deleteGuildEmoji(guildId: Long, emojiId: Long) =
        Route.delete<Unit>("/guilds/$guildId/emojis/$emojiId")

//#############################################################################

    fun createGuild() =
        Route.post<GuildResponse>("/guilds")

    fun getGuild(guildId: Long) =
        Route.get<GuildResponse>("/guilds/$guildId")

    fun modifyGuild(guildId: Long) =
        Route.patch<GuildResponse>("/guilds/$guildId")

    fun deleteGuild(guildId: Long) =
        Route.delete<Unit>("/guilds/$guildId")

    fun getGuildChannels(guildId: Long) =
        Route.get<Array<ChannelResponse>>("/guilds/$guildId/channels")

    fun createGuildChannel(guildId: Long) =
        Route.post<ChannelResponse>("/guilds/$guildId/channels")

    fun modifyGuildChannelPositions(guildId: Long) =
        Route.patch<Unit>("/guilds/$guildId/channels")

    fun getMember(guildId: Long, userId: Long) =
        Route.get<GuildMemberResponse>("/guilds/$guildId/members/$userId")

    fun getMembers(guildId: Long) =
        Route.get<Array<GuildMemberResponse>>("/guilds/$guildId/members")

    fun addMember(guildId: Long, userId: Long) =
        Route.put<GuildMemberResponse>("/guilds/$guildId/members/$userId")

    fun modifyMember(guildId: Long, userId: Long) =
        Route.patch<Unit>("/guilds/$guildId/members/$userId")

    fun modifyOwnNickname(guildId: Long) =
        Route.patch<NicknameModifyResponse>("/guilds/$guildId/members/@me/nick")

    fun addMemberRole(guildId: Long, userId: Long, roleId: Long) =
        Route.put<Unit>("/guilds/$guildId/members/$userId/roles/$roleId")

    fun removeMemberRole(guildId: Long, userId: Long, roleId: Long) =
        Route.delete<Unit>("/guilds/$guildId/members/$userId/roles/$roleId")

    fun removeMember(guildId: Long, userId: Long) =
        Route.delete<Unit>("/guilds/$guildId/members/$userId")

    fun getBans(guildId: Long) =
        Route.get<Array<BanResponse>>("/guilds/$guildId/bans")

    fun getBan(guildId: Long, userId: Long) =
        Route.get<BanResponse>("/guilds/$guildId/bans/$userId")

    fun createBan(guildId: Long, userId: Long) =
        Route.put<Unit>("/guilds/$guildId/bans/$userId")

    fun removeBan(guildId: Long, userId: Long) =
        Route.delete<Unit>("/guilds/$guildId/bans/$userId")

    fun getRoles(guildId: Long) =
        Route.get<Array<RoleResponse>>("/guilds/$guildId/roles")

    fun createRole(guildId: Long) =
        Route.post<RoleResponse>("/guilds/$guildId/roles")

    fun modifyRolePositions(guildId: Long) =
        Route.patch<Array<RoleResponse>>("/guilds/$guildId/roles")

    fun modifyRole(guildId: Long, roleId: Long) =
        Route.patch<RoleResponse>("/guilds/$guildId/roles/$roleId")

    fun deleteRole(guildId: Long, roleId: Long) =
        Route.delete<Unit>("/guilds/$guildId/roles/$roleId")

    fun getPruneCount(guildId: Long) =
        Route.get<PruneResponse>("/guilds/$guildId/prune")

    fun beginPrune(guildId: Long) =
        Route.post<PruneResponse>("/guilds/$guildId/prune")

    fun getVoiceRegions(guildId: Long) =
        Route.get<Array<VoiceRegionResponse>>("/guilds/$guildId/regions")

    fun getInvites(guildId: Long) =
        Route.get<Array<InviteResponse>>("/guilds/$guildId/invites")

    fun getIntegrations(guildId: Long) =
        Route.get<Array<GuildIntegrationResponse>>("/guilds/$guildId/integrations")

    fun createIntegration(guildId: Long) =
        Route.post<Unit>("/guilds/$guildId/integrations")

    fun modifyIntegration(guildId: Long, integrationId: Long) =
        Route.patch<Unit>("/guilds/$guildId/integrations/$integrationId")

    fun deleteIntegration(guildId: Long, integrationId: Long) =
        Route.delete<Unit>("/guilds/$guildId/integrations/$integrationId")

    fun syncIntegration(guildId: Long, integrationId: Long) =
        Route.post<Unit>("/guilds/$guildId/integrations/$integrationId")

    fun getEmbed(guildId: Long) =
        Route.get<GuildEmbedResponse>("/guilds/$guildId/embed")

    fun modifyEmbed(guildId: Long) =
        Route.patch<GuildEmbedResponse>("/guilds/$guildId/embed")

//#############################################################################


    fun getInvite(inviteCode: String) =
        Route.get<InviteResponse>("/invites/$inviteCode")

    fun deleteInvite(inviteCode: String) =
        Route.delete<Unit>("/invites/$inviteCode")

    fun acceptInvite(inviteCode: String) =
        Route.post<Unit>("/invites/$inviteCode")

//#############################################################################

    fun getCurrentUser() =
        Route.get<UserResponse>("/users/@me")

    fun getUser(userId: Long) =
        Route.get<UserResponse>("/users/$userId")

    fun modifyCurrentUser() =
        Route.patch<UserResponse>("/users/@me")

    fun getCurrentUserGuilds() =
        Route.get<Array<UserGuildResponse>>("/users/@me/guilds")

    fun leaveGuild(guildId: Long) =
        Route.delete<Unit>("/users/@me/guilds/$guildId")

    fun getDMs() =
        Route.get<Array<ChannelResponse>>("/users/@me/channels")

    fun createDM() =
        Route.post<ChannelResponse>("/users/@me/channels")

    fun createGroupDM() =
        Route.post<ChannelResponse>("/users/@me/channels")

    fun getConnections() =
        Route.get<Array<ConnectionResponse>>("/users/@me/connections")

//#############################################################################

    fun getVoiceRegions() =
        Route.get<Array<VoiceRegionResponse>>("/voice/regions")

//#############################################################################

    fun createChannelWebhook(channelId: Long) =
        Route.post<WebhookResponse>("/channels/$channelId/webhooks")

    fun getChannelWebhooks(channelId: Long) =
        Route.get<Array<WebhookResponse>>("/channels/$channelId/webhooks")

    fun getGuildWebhooks(guildId: Long) =
        Route.get<Array<WebhookResponse>>("/guilds/$guildId/webhooks")

    fun getWebhook(webhookId: Long) =
        Route.get<WebhookResponse>("/webhooks/$webhookId")

    fun getWebhook(webhookId: Long, webhookToken: String) =
        Route.get<WebhookResponse>("/webhooks/$webhookId/$webhookToken")

    fun modifyWebhook(webhookId: Long) =
        Route.patch<WebhookResponse>("/webhooks/$webhookId")

    fun modifyWebhook(webhookId: Long, webhookToken: String) =
        Route.patch<WebhookResponse>("/webhooks/$webhookId/$webhookToken")

    fun deleteWebhook(webhookId: Long) =
        Route.delete<Unit>("/webhooks/$webhookId")

    fun deleteWebhook(webhookId: Long, webhookToken: String) =
        Route.delete<Unit>("/webhooks/$webhookId/$webhookToken")

    fun executeWebhook(webhookId: Long, webhookToken: String) =
        Route.post<Unit>("/webhooks/$webhookId/$webhookToken")

    fun executeSlackComparableWebhook(webhookId: Long, webhookToken: String) =
        Route.post<Unit>("/webhooks/$webhookId/$webhookToken/slack")

    fun executeGithubComparableWebhook(webhookId: Long, webhookToken: String) =
        Route.post<Unit>("/webhooks/$webhookId/$webhookToken/github")

    fun getGateway() =
        Route.get<GatewayResponse>("/gateway")

    fun getGatewayBot() =
        Route.get<GatewayBotResponse>("/gateway/bot")
}