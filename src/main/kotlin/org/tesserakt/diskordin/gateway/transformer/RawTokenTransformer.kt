package org.tesserakt.diskordin.gateway.transformer

import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened

object RawTokenTransformer :
    Transformer<Payload<IToken>, IToken> {
    override fun transform(context: Payload<IToken>): IToken = when (context.name) {
        "CONNECTION_OPENED" -> context.unwrap<ConnectionOpened>()
        "CONNECTION_CLOSING" -> context.unwrap<ConnectionClosing>()
        "CONNECTION_CLOSED" -> context.unwrap<ConnectionClosed>()
        "CONNECTION_FAILED" -> context.unwrap<ConnectionFailed>()
        else -> throw IllegalStateException("Should never raises")
    }
}