package org.tesserakt.diskordin.core.data.json.request


data class WebhookCreateRequest(
    val name: String,
    val avatar: String?
) : JsonRequest()
