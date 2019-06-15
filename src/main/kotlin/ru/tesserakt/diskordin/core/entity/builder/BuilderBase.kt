package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.JsonRequest
import kotlin.reflect.full.createInstance

abstract class BuilderBase<R : JsonRequest> internal constructor() {
    abstract fun create(): R
}

inline fun <R : JsonRequest, reified B : BuilderBase<R>> (B.() -> Unit).build() =
    B::class.createInstance().apply(this).create()

abstract class AuditLogging<R : JsonRequest> : BuilderBase<R>() {
    abstract var reason: String?
}

inline fun <reified AB : AuditLogging<*>> (AB.() -> Unit).extractReason() =
    AB::class.createInstance().apply(this).reason
