import io.ktor.util.*
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.impl.core.client.configure
import org.tesserakt.diskordin.rest.WithoutRest

@KtorExperimentalAPI
@DiscordClientBuilderScope.InternalTestAPI
suspend fun main(args: Array<String>) {
    val client = DiscordClientBuilder by WithoutRest configure {
        +token(args[0])
    }

    client.login()
}