package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.JsonRequest
import kotlin.reflect.full.createInstance

abstract class BuilderBase<R : JsonRequest> internal constructor() {
    abstract fun create(): R
}

inline fun <R : JsonRequest, reified B : BuilderBase<out R>> (B.() -> Unit).build() =
    instance().create()

inline fun <R : JsonRequest, reified B : BuilderBase<out R>> (B.() -> Unit).instance() =
    B::class.createInstance().apply(this)

abstract class AuditLogging<R : JsonRequest> : BuilderBase<R>() {
    abstract var reason: String?
}
