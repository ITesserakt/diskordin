package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.PositionEditRequest
import kotlin.properties.Delegates

class PositionEditBuilder : BuilderBase<PositionEditRequest>() {
    lateinit var id: Snowflake
    var position by Delegates.notNull<Int>()

    override fun create(): PositionEditRequest = PositionEditRequest(
        id, position
    )
}
