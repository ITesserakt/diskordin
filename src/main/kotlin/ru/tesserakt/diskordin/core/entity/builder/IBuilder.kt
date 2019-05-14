package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.JsonRequest

interface IBuilder<R : JsonRequest> {
    fun create(): R
}

interface IAuditLogging<R : JsonRequest> : IBuilder<R> {
    var reason: String?
}
