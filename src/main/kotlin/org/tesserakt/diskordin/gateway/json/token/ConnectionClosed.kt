package org.tesserakt.diskordin.gateway.json.token

import org.tesserakt.diskordin.gateway.json.IToken

data class ConnectionClosed(
    val code: Short,
    val reason: String
) : IToken