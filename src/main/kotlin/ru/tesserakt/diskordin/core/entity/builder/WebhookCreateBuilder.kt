package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest

class WebhookCreateBuilder : IAuditLogging<WebhookCreateRequest> {
    lateinit var name: String
    var avatar: String? = null

    override fun create(): WebhookCreateRequest = WebhookCreateRequest(
        name,
        avatar
    )

    override var reason: String? = null
}