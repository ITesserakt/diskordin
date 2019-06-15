package ru.tesserakt.diskordin.core.entity.builder

import kotlin.properties.Delegates

class PositionEditBuilder : BuilderBase<PositionEditRequest>() {
    lateinit var id: Snowflake
    var position by Delegates.notNull<Int>()

    override fun create(): PositionEditRequest = PositionEditRequest(
        id.asLong(), position
    )
}
