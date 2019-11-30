package org.tesserakt.diskordin.core.client

import arrow.core.Either
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload

abstract class WebSocketStateHolder {
    abstract fun getState(): IToken
    internal abstract fun update(payload: Payload<in IToken>): Either<IllegalStateException, Unit>
    abstract fun observe(block: (old: IToken, new: IToken) -> Unit)
}