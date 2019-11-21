package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.JsonRequest
import kotlin.reflect.full.createInstance

abstract class BuilderBase<R : JsonRequest> internal constructor() {
    internal abstract fun create(): R
}

internal inline fun <R : JsonRequest, reified B : BuilderBase<out R>> (B.() -> Unit).build() =
    instance().create()

inline fun <R : JsonRequest, reified B : BuilderBase<out R>> (B.() -> Unit).instance() =
    B::class.createInstance().apply(this)

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
