package ru.tesserakt.diskordin.util

import arrow.data.Store
import kotlinx.coroutines.Deferred
import ru.tesserakt.diskordin.core.data.Snowflake

typealias Identified<T> = Store<Snowflake, Deferred<T>>
