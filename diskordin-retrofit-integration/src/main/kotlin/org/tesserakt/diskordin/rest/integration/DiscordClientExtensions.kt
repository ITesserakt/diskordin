package org.tesserakt.diskordin.rest.integration

import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.RetrofitScope

suspend fun DiscordClientBuilder.retrofitBackend(block: RetrofitScope.() -> Unit) =
    invoke(::RetrofitScope, block)