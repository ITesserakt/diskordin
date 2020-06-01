package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake

data class MessageCreateRequest(
    val content: String?,
    val nonce: Long? = null,
    val tts: Boolean? = null,
    val embed: EmbedCreateRequest? = null,
    val allowedMentions: AllowedMentionsRequest? = null
) : JsonRequest() {
    data class AllowedMentionsRequest(
        val parse: List<String>,
        val roles: List<Snowflake>,
        val users: List<Snowflake>
    ) : JsonRequest() {
        enum class AllowedMentionTypes(val value: String) {
            Role("roles"), User("users"), Everyone("everyone")
        }
    }
}