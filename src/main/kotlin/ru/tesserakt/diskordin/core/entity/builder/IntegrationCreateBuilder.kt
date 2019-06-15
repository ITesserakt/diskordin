package ru.tesserakt.diskordin.core.entity.builder

class IntegrationCreateBuilder : BuilderBase<IntegrationCreateRequest>() {
    lateinit var type: String
    lateinit var id: Snowflake

    override fun create(): IntegrationCreateRequest = IntegrationCreateRequest(
        type,
        id.asLong()
    )
}
