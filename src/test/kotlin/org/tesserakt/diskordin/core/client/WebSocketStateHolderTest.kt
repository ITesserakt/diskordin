package org.tesserakt.diskordin.core.client

import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.json.token.NoConnection
import org.tesserakt.diskordin.impl.core.client.WebSocketStateHolderImpl
import org.tesserakt.diskordin.util.toJsonTree

internal class WebSocketStateHolderTest {
    private val stateHolder = WebSocketStateHolderImpl()
    private val first = Payload<IToken>(
        -1,
        null,
        "CONNECTION_OPENED",
        ConnectionOpened.toJsonTree()
    )
    private val second = Payload<IToken>(
        -1,
        null,
        "CONNECTION_FAILED",
        ConnectionFailed(IllegalStateException("Test")).toJsonTree()
    )

    @Test
    fun `update and get new state`() {
        val initialState = stateHolder.getState()
        initialState `should equal` NoConnection

        val firstUpdate = stateHolder.update(first)
        firstUpdate.isRight().shouldBeTrue()
        var stateAfterUpd = stateHolder.getState()
        stateAfterUpd `should be instance of` ConnectionOpened::class

        val secondUpdate = stateHolder.update(second)
        secondUpdate.isRight().shouldBeTrue()
        stateAfterUpd = stateHolder.getState()
        stateAfterUpd `should be instance of` ConnectionFailed::class
    }
}