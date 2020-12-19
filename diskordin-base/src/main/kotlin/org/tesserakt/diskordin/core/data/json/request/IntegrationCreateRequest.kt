package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake


data class IntegrationCreateRequest(
    val type: String,
    val id: Snowflake
) : JsonRequest()
