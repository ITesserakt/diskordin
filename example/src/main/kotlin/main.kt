import arrow.core.getOrHandle
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.rest.integration.Retrofit

@DiscordClientBuilderScope.InternalTestAPI
suspend fun main(args: Array<String>) {
    val client = DiscordClientBuilder[Retrofit] {
        +token(args[0])
        +gatewaySettings {
            +compressShards()
        }
    }.getOrHandle { error(it) }

    client.login()
}