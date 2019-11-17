package org.tesserakt.diskordin.core.data.json.request


data class WebhookEditRequest(
    val name: String? = null,
    val avatar: String? = null
) : JsonRequest()
