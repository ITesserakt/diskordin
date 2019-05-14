@file:Suppress("unused")

package ru.tesserakt.diskordin.core.rest

internal object Routes {

//#############################################################################

    fun getChannel(channelId: Long) =
        Route.get("/channels/$channelId")

    fun modifyChannel(channelId: Long) =
        Route.put("/channels/$channelId")

    fun partialModifyChannel(channelId: Long) =
        Route.patch("/channels/$channelId")

    fun closeChannel(channelId: Long) =
        Route.delete("/channels/$channelId")

    fun getMessages(channelId: Long) =
        Route.get("/channels/$channelId/messages")

    fun getMessage(channelId: Long, messageId: Long) =
        Route.get("/channels/$channelId/messages/$messageId")

    fun createMessage(channelId: Long) =
        Route.post("/channels/$channelId/messages")

    fun createReaction(channelId: Long, messageId: Long, emoji: String) =
        Route.put("/channels/$channelId/messages/$messageId/reactions/$emoji/@me")

    fun deleteOwnReaction(channelId: Long, messageId: Long, emoji: String) =
        Route.delete("/channels/$channelId/messages/$messageId/reactions/$emoji/@me")

    fun deleteReaction(channelId: Long, messageId: Long, emoji: String, userId: Long) =
        Route.delete("/channels/$channelId/messages/$messageId/reactions/$emoji/$userId")

    fun getReactions(channelId: Long, messageId: Long, emoji: String) =
        Route.get("/channels/$channelId/messages/$messageId/reactions/$emoji")

    fun deleteAllReactions(channelId: Long, messageId: Long) =
        Route.delete("/channels/$channelId/messages/$messageId/reactions")

    fun editMessage(channelId: Long, messageId: Long) =
        Route.patch("/channels/$channelId/messages/$messageId")

    fun deleteMessage(channelId: Long, messageId: Long) =
        Route.delete("/channels/$channelId/messages/$messageId")

    fun bulkDeleteMessages(channelId: Long) =
        Route.post("/channels$channelId/messages/bulk-delete")

    fun editChannelPermissions(channelId: Long, overwriteId: Long) =
        Route.put("/channels/$channelId/permissions/$overwriteId")

    fun getChannelInvites(channelId: Long) =
        Route.get("/channels/$channelId/invites")

    fun createChannelInvite(channelId: Long) =
        Route.post("/channels/$channelId/invites")

    fun deleteChannelPermission(channelId: Long, overwriteId: Long) =
        Route.delete("/channels/$channelId/permissions/$overwriteId")

    fun triggerTypingIndicator(channelId: Long) =
        Route.post("/channels/$channelId/typing")

    fun getPinnedMessages(channelId: Long) =
        Route.get("/channels/$channelId/pins")

    fun addPinnedMessage(channelId: Long, messageId: Long) =
        Route.put("/channels/$channelId/pins/$messageId")

    fun deletePinnedMessage(channelId: Long, messageId: Long) =
        Route.delete("/channels/$channelId/pins/$messageId")

    fun addGroupDMRecipient(channelId: Long, userId: Long) =
        Route.put("/channels/$channelId/recipients/$userId")

    fun deleteGroupDMRecipient(channelId: Long, userId: Long) =
        Route.delete("/channels/$channelId/recipients/$userId")

//#############################################################################

    fun getGuildEmojis(guildId: Long) =
        Route.get("/guilds/$guildId/emojis")

    fun getGuildEmoji(guildId: Long, emojiId: Long) =
        Route.get("/guilds/$guildId/emojis/$emojiId")

    fun createGuildEmoji(guildId: Long) =
        Route.post("/guilds/$guildId/emojis")

    fun modifyGuildEmoji(guildId: Long, emojiId: Long) =
        Route.patch("/guilds/$guildId/emojis/$emojiId")

    fun deleteGuildEmoji(guildId: Long, emojiId: Long) =
        Route.delete("/guilds/$guildId/emojis/$emojiId")

//#############################################################################

    fun createGuild() =
        Route.post("/guilds")

    fun getGuild(guildId: Long) =
        Route.get("/guilds/$guildId")

    fun modifyGuild(guildId: Long) =
        Route.patch("/guilds/$guildId")

    fun deleteGuild(guildId: Long) =
        Route.delete("/guilds/$guildId")

    fun getGuildChannels(guildId: Long) =
        Route.get("/guilds/$guildId/channels")

    fun createGuildChannel(guildId: Long) =
        Route.post("/guilds/$guildId/channels")

    fun modifyGuildChannelPositions(guildId: Long) =
        Route.patch("/guilds/$guildId/channels")

    fun getMember(guildId: Long, userId: Long) =
        Route.get("/guilds/$guildId/members/$userId")

    fun getMembers(guildId: Long) =
        Route.get("/guilds/$guildId/members")

    fun addMember(guildId: Long, userId: Long) =
        Route.put("/guilds/$guildId/members/$userId")

    fun modifyMember(guildId: Long, userId: Long) =
        Route.patch("/guilds/$guildId/members/$userId")

    fun modifyOwnNickname(guildId: Long) =
        Route.patch("/guilds/$guildId/members/@me/nick")

    fun addMemberRole(guildId: Long, userId: Long, roleId: Long) =
        Route.put("/guilds/$guildId/members/$userId/roles/$roleId")

    fun removeMemberRole(guildId: Long, userId: Long, roleId: Long) =
        Route.delete("/guilds/$guildId/members/$userId/roles/$roleId")

    fun removeMember(guildId: Long, userId: Long) =
        Route.delete("/guilds/$guildId/members/$userId")

    fun getBans(guildId: Long) =
        Route.get("/guilds/$guildId/bans")

    fun getBan(guildId: Long, userId: Long) =
        Route.get("/guilds/$guildId/bans/$userId")

    fun createBan(guildId: Long, userId: Long) =
        Route.put("/guilds/$guildId/bans/$userId")

    fun removeBan(guildId: Long, userId: Long) =
        Route.delete("/guilds/$guildId/bans/$userId")

    fun getRoles(guildId: Long) =
        Route.get("/guilds/$guildId/roles")

    fun createRole(guildId: Long) =
        Route.post("/guilds/$guildId/roles")

    fun modifyRolePositions(guildId: Long) =
        Route.patch("/guilds/$guildId/roles")

    fun modifyRole(guildId: Long, roleId: Long) =
        Route.patch("/guilds/$guildId/roles/$roleId")

    fun deleteRole(guildId: Long, roleId: Long) =
        Route.delete("/guilds/$guildId/roles/$roleId")

    fun getPruneCount(guildId: Long) =
        Route.get("/guilds/$guildId/prune")

    fun beginPrune(guildId: Long) =
        Route.post("/guilds/$guildId/prune")

    fun getVoiceRegions(guildId: Long) =
        Route.get("/guilds/$guildId/regions")

    fun getInvites(guildId: Long) =
        Route.get("/guilds/$guildId/invites")

    fun getIntegrations(guildId: Long) =
        Route.get("/guilds/$guildId/integrations")

    fun createIntegration(guildId: Long) =
        Route.post("/guilds/$guildId/integrations")

    fun modifyIntegration(guildId: Long, integrationId: Long) =
        Route.patch("/guilds/$guildId/integrations/$integrationId")

    fun deleteIntegration(guildId: Long, integrationId: Long) =
        Route.delete("/guilds/$guildId/integrations/$integrationId")

    fun syncIntegration(guildId: Long, integrationId: Long) =
        Route.post("/guilds/$guildId/integrations/$integrationId")

    fun getEmbed(guildId: Long) =
        Route.get("/guilds/$guildId/embed")

    fun modifyEmbed(guildId: Long) =
        Route.patch("/guilds/$guildId/embed")

//#############################################################################


    fun getInvite(inviteCode: String) =
        Route.get("/invites/$inviteCode")

    fun deleteInvite(inviteCode: String) =
        Route.delete("/invites/$inviteCode")

    fun acceptInvite(inviteCode: String) =
        Route.post("/invites/$inviteCode")

//#############################################################################

    fun getCurrentUser() =
        Route.get("/users/@me")

    fun getUser(userId: Long) =
        Route.get("/users/$userId")

    fun modifyCurrentUser() =
        Route.patch("/users/@me")

    fun getCurrentUserGuilds() =
        Route.get("/users/@me/guilds")

    fun leaveGuild(guildId: Long) =
        Route.delete("/users/@me/guilds/$guildId")

    fun getDMs() =
        Route.get("/users/@me/channels")

    fun createDM() =
        Route.post("/users/@me/channels")

    fun createGroupDM() =
        Route.post("/users/@me/channels")

    fun getConnections() =
        Route.get("/users/@me/connections")

//#############################################################################

    fun getVoiceRegions() =
        Route.get("/voice/regions")

//#############################################################################

    fun createChannelWebhook(channelId: Long) =
        Route.post("/channels/$channelId/webhooks")

    fun getChannelWebhooks(channelId: Long) =
        Route.get("/channels/$channelId/webhooks")

    fun getGuildWebhooks(guildId: Long) =
        Route.get("/guilds/$guildId/webhooks")

    fun getWebhook(webhookId: Long) =
        Route.get("/webhooks/$webhookId")

    fun getWebhook(webhookId: Long, webhookToken: String) =
        Route.get("/webhooks/$webhookId/$webhookToken")

    fun modifyWebhook(webhookId: Long) =
        Route.patch("/webhooks/$webhookId")

    fun modifyWebhook(webhookId: Long, webhookToken: String) =
        Route.patch("/webhooks/$webhookId/$webhookToken")

    fun deleteWebhook(webhookId: Long) =
        Route.delete("/webhooks/$webhookId")

    fun deleteWebhook(webhookId: Long, webhookToken: String) =
        Route.delete("/webhooks/$webhookId/$webhookToken")

    fun executeWebhook(webhookId: Long, webhookToken: String) =
        Route.post("/webhooks/$webhookId/$webhookToken")

    fun executeSlackComparableWebhook(webhookId: Long, webhookToken: String) =
        Route.post("/webhooks/$webhookId/$webhookToken/slack")

    fun executeGithubComparableWebhook(webhookId: Long, webhookToken: String) =
        Route.post("/webhooks/$webhookId/$webhookToken/github")
}