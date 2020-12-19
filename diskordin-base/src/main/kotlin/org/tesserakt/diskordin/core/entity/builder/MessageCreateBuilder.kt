@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import arrow.core.Ior
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.MessageCreateRequest
import org.tesserakt.diskordin.core.data.json.request.MessageCreateRequest.AllowedMentionsRequest.AllowedMentionTypes

typealias Content = String
typealias Embed = EmbedCreateBuilder

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class MessageCreateBuilder(private val required: Ior<Content, Embed>) : BuilderBase<MessageCreateRequest>() {
    private var content: String? = null
    private var nonce: Snowflake? = null
    private var tts: Boolean? = null
    private var embed: EmbedCreateBuilder? = null
    private var allowedMentions: MessageCreateRequest.AllowedMentionsRequest? = null

    @RequestBuilder
    class AllowedMentionsBuilder : BuilderBase<MessageCreateRequest.AllowedMentionsRequest>() {
        private val parse = mutableSetOf<AllowedMentionTypes>()
        private val roles = mutableSetOf<Snowflake>()
        private val users = mutableSetOf<Snowflake>()

        operator fun Array<out AllowedMentionTypes>.unaryPlus() {
            parse += this.toSet()
        }

        operator fun Array<Snowflake>.unaryPlus() {
            require(this.size <= 100)

            users += this.toSet()
        }

        operator fun List<Snowflake>.unaryPlus() {
            require(this.size <= 100)

            roles += this.toSet()
        }

        inline fun AllowedMentionsBuilder.allowOnly(vararg types: AllowedMentionTypes) = types
        inline fun AllowedMentionsBuilder.allowUsers(userIds: Array<Snowflake>) = userIds
        inline fun AllowedMentionsBuilder.allowRoles(roleIds: List<Snowflake>) = roleIds
        inline fun AllowedMentionsBuilder.suppressAll() {
            +allowOnly()
        }

        override fun create() = MessageCreateRequest.AllowedMentionsRequest(
            parse.map { it.value },
            roles.toList(),
            users.toList()
        )
    }

    override fun create(): MessageCreateRequest {
        required.fold({
            content = it
        }, {
            embed = it
        }, { c, e ->
            content = c
            embed = e
        })

        return MessageCreateRequest(
            content,
            nonce,
            tts,
            embed?.create(),
            allowedMentions
        )
    }

    operator fun Snowflake.unaryPlus() {
        nonce = this
    }

    operator fun Unit.unaryPlus() {
        tts = true
    }

    operator fun MessageCreateRequest.AllowedMentionsRequest.unaryPlus() {
        allowedMentions = this
    }

    inline fun MessageCreateBuilder.nonce(id: Snowflake) = id
    inline fun MessageCreateBuilder.enableTTS() = Unit
    inline fun MessageCreateBuilder.mentionRules(builder: AllowedMentionsBuilder.() -> Unit) =
        AllowedMentionsBuilder().apply(builder).create()
}