package ru.tesserakt.diskordin.core.data.json.response


data class UserResponse(
    val id: Long,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: Boolean? = null,
    val mfa_enabled: Boolean? = null,
    val locale: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val flags: Int? = null,
    val premium_type: Int? = null
) : DiscordResponse()