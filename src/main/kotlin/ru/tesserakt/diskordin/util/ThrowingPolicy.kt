package ru.tesserakt.diskordin.util

import io.ktor.util.error
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

enum class ThrowingPolicy {
    Log {
        override fun <T : Any> handle(exception: Throwable): T =
            LoggingThrowingStrategy<T>(LoggerFactory.getLogger("Exception Eater")).resolve(exception)
    },
    Rethrow {
        override fun <T : Any> handle(exception: Throwable): T =
            RethrowingStrategy<T>().resolve(exception)
    };

    internal abstract fun <T : Any> handle(exception: Throwable): T
}

internal interface IThrowingStrategy<T> {
    fun resolve(exception: Throwable): T
}

internal class LoggingThrowingStrategy<T : Any>(private val parentLogger: Logger) : IThrowingStrategy<T> {
    private val stub: Any = ""

    override fun resolve(exception: Throwable): T {
        parentLogger.error(exception)

        //how to eat exceptions on breakfast
        Delegates.notNull<String>().setValue(null, ::stub, "")
        return Delegates.notNull<T>().getValue(null, ::stub)
    }
}

internal class RethrowingStrategy<T> : IThrowingStrategy<T> {
    override fun resolve(exception: Throwable): T = throw exception
}
