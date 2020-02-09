package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.json.response.ActivityResponse

data class UserStatusUpdateRequest(
    val since: Long?,
    val game: ActivityResponse?,
    val status: String,
    val afk: Boolean
) : JsonRequest()