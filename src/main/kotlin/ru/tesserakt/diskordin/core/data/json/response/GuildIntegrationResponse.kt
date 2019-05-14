package ru.tesserakt.diskordin.core.data.json.response


data class GuildIntegrationResponse(
    val id: Long,
    val name: String,
    val type: String,
    val enabled: Boolean,
    val syncing: Boolean,
    val role_id: Long,
    val expire_behavior: Int,
    val expire_grace_period: Int,
    val user: UserResponse,
    val account: AccountResponse,
    val synced_at: String
) : DiscordResponse()