@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import arrow.core.Ior
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.MessageCreateRequest

typealias Content = String
typealias Embed = EmbedCreateBuilder

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class MessageCreateBuilder(private val required: Ior<Content, Embed>) : BuilderBase<MessageCreateRequest>() {
    private var content: String? = null
    private var nonce: Snowflake? = null
    private var tts: Boolean? = null
    private var embed: EmbedCreateBuilder? = null

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
            content!!,
            nonce?.asLong(),
            tts,
            embed?.create()
        )
    }

    operator fun Snowflake.unaryPlus() {
        nonce = this
    }

    operator fun Boolean.unaryPlus() {
        tts = this
    }

    inline fun MessageCreateBuilder.nonce(id: Snowflake) = id
    inline fun MessageCreateBuilder.tts(value: Boolean) = value
}