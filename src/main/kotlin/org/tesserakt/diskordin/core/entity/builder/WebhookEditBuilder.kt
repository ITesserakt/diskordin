package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest

class WebhookEditBuilder : AuditLogging<WebhookEditRequest>() {
    var name: String? = null
    var avatar: String? = null

    override fun create(): WebhookEditRequest = WebhookEditRequest(
        name,
        avatar
    )

    override var reason: String? = null
}