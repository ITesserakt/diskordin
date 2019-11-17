package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake


data class PositionEditRequest(
    val id: Snowflake,
    val position: Int
) : JsonRequest()
