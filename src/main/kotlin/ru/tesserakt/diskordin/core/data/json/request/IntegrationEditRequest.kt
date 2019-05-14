package ru.tesserakt.diskordin.core.data.json.request


data class IntegrationEditRequest(
    val expire_behaviour: Int,
    val expire_grace_period: Int? = null,
    val enable_emoticons: Boolean? = null
) : JsonRequest()
