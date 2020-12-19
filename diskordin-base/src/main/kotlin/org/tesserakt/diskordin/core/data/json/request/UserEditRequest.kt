package org.tesserakt.diskordin.core.data.json.request


data class UserEditRequest(
    val username: String,
    val avatar: String
) : JsonRequest()
