package org.tesserakt.diskordin.gateway

import arrow.fx.coroutines.CancelToken
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.cancellable
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import java.lang.reflect.Type

class ArrowStreamAdapter<T> : StreamAdapter<T, Stream<T>> {
    override fun adapt(stream: com.tinder.scarlet.Stream<T>): Stream<T> = Stream.cancellable {
        val disposable = stream.start(object : com.tinder.scarlet.Stream.Observer<T> {
            override fun onComplete() = end()

            override fun onError(throwable: Throwable) = throw throwable

            override fun onNext(data: T) = emit(data)
        })

        CancelToken { disposable.dispose() }
    }

    companion object Factory : StreamAdapter.Factory {
        override fun create(type: Type): StreamAdapter<Any, Any> = when (type.getRawType()) {
            Stream::class.java -> ArrowStreamAdapter()
            else -> throw IllegalArgumentException("No adapter for type $type")
        }
    }
}