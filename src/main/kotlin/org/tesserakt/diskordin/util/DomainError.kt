package org.tesserakt.diskordin.util

abstract class DomainError

data class GenericError(val cause: Throwable) : DomainError()

data class NetError(val code: Int, val message: String) : DomainError()