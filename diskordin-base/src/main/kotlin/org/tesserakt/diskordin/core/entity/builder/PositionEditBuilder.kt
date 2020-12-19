package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.PositionEditRequest

@RequestBuilder
class PositionEditBuilder(val id: Snowflake, val position: Int) : BuilderBase<PositionEditRequest>() {
    override fun create(): PositionEditRequest = PositionEditRequest(
        id, position
    )
}
