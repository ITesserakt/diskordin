package org.tesserakt.diskordin.gateway.json.token

import org.tesserakt.diskordin.gateway.json.IToken

data class ConnectionClosing(
    val code: Short,
    val reason: String
) : IToken
