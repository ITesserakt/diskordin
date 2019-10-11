package ru.tesserakt.diskordin.core.data.json.response


data class NicknameModifyResponse(
    val nick: String? = null
) : DiscordResponse() {
    fun unwrap() = nick
}