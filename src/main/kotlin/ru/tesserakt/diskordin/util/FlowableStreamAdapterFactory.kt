package ru.tesserakt.diskordin.util

import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import io.reactivex.Flowable
import java.lang.reflect.Type

class FlowableStreamAdapter<T> : StreamAdapter<T, Flowable<T>> {
    override fun adapt(stream: Stream<T>): Flowable<T> = Flowable.fromPublisher(stream)

    class Factory : StreamAdapter.Factory {
        override fun create(type: Type): StreamAdapter<Any, Any> = when (type.getRawType()) {
            Flowable::class.java -> FlowableStreamAdapter()
            else -> throw IllegalStateException()
        }
    }
}
