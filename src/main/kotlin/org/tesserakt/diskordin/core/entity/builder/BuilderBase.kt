package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.JsonRequest

abstract class BuilderBase<R : JsonRequest> internal constructor() {
    abstract fun create(): R
}

internal inline fun <R : JsonRequest, B : BuilderBase<out R>> (B.() -> Unit).build(ctor: () -> B) =
    instance(ctor).create()

inline fun <R : JsonRequest, B : BuilderBase<out R>> (B.() -> Unit).instance(ctor: () -> B) =
    ctor().apply(this)

@Suppress("NOTHING_TO_INLINE", "unused")
abstract class AuditLogging<R : JsonRequest> : BuilderBase<R>() {
    var reason: String? = null
        private set

    operator fun Reason.unaryPlus() {
        reason = this.v
    }

    inline fun AuditLogging<*>.reason(reason: String) = Reason(reason)
}

@DslMarker
annotation class RequestBuilder
