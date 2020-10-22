package org.tesserakt.diskordin.util

import arrow.fx.coroutines.CancelToken
import arrow.fx.coroutines.stream.cancellable
import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import java.lang.reflect.Type
import arrow.fx.coroutines.stream.Stream as AStream

class StreamAdapter<T> : StreamAdapter<T, AStream<T>> {
    override fun adapt(stream: Stream<T>): AStream<T> = AStream.cancellable<T> {
        val observer = object : Stream.Observer<T> {
            override fun onComplete() {
                end()
            }

            override fun onError(throwable: Throwable) {
                throw throwable
            }

            override fun onNext(data: T) {
                emit(data)
            }
        }

        stream.start(observer)
        CancelToken.unit
    }

    companion object Factory : StreamAdapter.Factory {
        override fun create(type: Type): StreamAdapter<Any, Any> = when (type.getRawType()) {
            AStream::class.java -> StreamAdapter()
            else -> throw IllegalStateException("$type is not supported.")
        }
    }
}