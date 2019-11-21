package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class WebhookCreateBuilder(val name: String) : AuditLogging<WebhookCreateRequest>() {
    private var avatar: String? = null

    operator fun String.unaryPlus() {
        avatar = this
    }

    inline fun WebhookCreateBuilder.avatar(url: String) = url

    override fun create(): WebhookCreateRequest = WebhookCreateRequest(
        name,
        avatar
    )
}