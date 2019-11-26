package org.tesserakt.diskordin.gateway.json.token

import okhttp3.Response
import org.tesserakt.diskordin.gateway.json.IToken

data class ConnectionOpened(
    val response: Response
) : IToken