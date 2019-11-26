package org.tesserakt.diskordin.gateway.json.token

import org.tesserakt.diskordin.gateway.json.IToken

data class ConnectionFailed(
    val error: Throwable
) : IToken
