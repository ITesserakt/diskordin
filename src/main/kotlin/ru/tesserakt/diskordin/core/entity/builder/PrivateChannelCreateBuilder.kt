package ru.tesserakt.diskordin.core.entity.builder

class PrivateChannelCreateBuilder : BuilderBase<DMCreateRequest>() {

    lateinit var recipientId: Snowflake


    override fun create(): DMCreateRequest = DMCreateRequest(
        recipientId.asLong()
    )
}