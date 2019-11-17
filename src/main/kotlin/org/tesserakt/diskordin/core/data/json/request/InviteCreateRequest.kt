package org.tesserakt.diskordin.core.data.json.request


data class InviteCreateRequest(
    val max_age: Int? = null,
    val max_usages: Int? = null,
    val temporary: Boolean? = null,
    val unique: Boolean? = null
) : JsonRequest()
