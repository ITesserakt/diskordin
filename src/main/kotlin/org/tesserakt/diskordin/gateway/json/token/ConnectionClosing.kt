package org.tesserakt.diskordin.gateway.json.token

import com.tinder.scarlet.websocket.ShutdownReason
import org.tesserakt.diskordin.gateway.json.IToken

data class ConnectionClosing(
    val reason: ShutdownReason
) : IToken
