package ru.tesserakt.diskordin.util

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IEntity

class Identified<T : IEntity>(val id: Snowflake, private val render: suspend (Snowflake) -> T) {
    suspend operator fun invoke() = render(id)
    operator fun component1() = id
    suspend operator fun component2() = this()

    fun update(newId: Snowflake) = Identified(newId, render)
}
