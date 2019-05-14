package ru.tesserakt.diskordin.util

import ru.tesserakt.diskordin.core.data.Snowflake

typealias Identified<T> = AsyncStore<Snowflake, T>
