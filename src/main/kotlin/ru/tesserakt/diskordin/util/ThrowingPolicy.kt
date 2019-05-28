package ru.tesserakt.diskordin.util

import arrow.core.Try
import io.ktor.util.error
import org.slf4j.Logger
import org.slf4j.LoggerFactory

enum class ThrowingPolicy {
    Verbose {
        override fun handle(exception: Throwable): Try<Nothing> {
            val logger: Logger = LoggerFactory.getLogger("Exceptions Eater")
            logger.error(exception)
            return Try.raiseError(exception)
        }
    },
    Quiet {
        override fun handle(exception: Throwable) = throw exception
    };

    internal abstract fun handle(exception: Throwable): Try<Nothing>
}
