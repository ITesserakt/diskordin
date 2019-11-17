package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake


data class DMCreateRequest(
    val recipient_id: Snowflake
) : JsonRequest()
