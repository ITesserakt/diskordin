package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class WebhookEditBuilder : AuditLogging<WebhookEditRequest>() {
    var name: String? = null
    var avatar: String? = null

    operator fun Name.unaryPlus() {
        name = this.v
    }

    operator fun String.unaryPlus() {
        avatar = this
    }

    inline fun WebhookEditBuilder.name(name: String) = Name(name)
    inline fun WebhookEditBuilder.avatar(url: String) = url

    override fun create(): WebhookEditRequest = WebhookEditRequest(
        name,
        avatar
    )
}