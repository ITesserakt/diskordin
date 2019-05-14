package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.WebhookEditRequest

class WebhookEditBuilder : IAuditLogging<WebhookEditRequest> {
    var name: String? = null
    var avatar: String? = null

    override fun create(): WebhookEditRequest = WebhookEditRequest(
        name,
        avatar
    )

    override var reason: String? = null
}