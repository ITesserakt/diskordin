package ru.tesserakt.diskordin.util

import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import java.lang.reflect.Type

class FlowStreamAdapter<T : Any> : StreamAdapter<T, Flow<T>> {
    override fun adapt(stream: Stream<T>): Flow<T> = stream.asFlow()

    class Factory : StreamAdapter.Factory {
        override fun create(type: Type): StreamAdapter<Any, Any> = when (type.getRawType()) {
            Flow::class.java -> FlowStreamAdapter()
            else -> throw IllegalStateException()
        }
    }
}