import io.ktor.client.engine.cio.*
import io.ktor.util.*
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.impl.core.client.configure
import org.tesserakt.diskordin.rest.integration.Ktor

@KtorExperimentalAPI
@DiscordClientBuilderScope.InternalTestAPI
suspend fun main(args: Array<String>) = (DiscordClientBuilder by Ktor(CIO) configure {
    +token(args[0])
}).login()