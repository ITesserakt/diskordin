import io.ktor.client.engine.cio.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import org.tesserakt.diskordin.core.cache.FileCacheSnapshot
import org.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.entity.cacheSnapshot
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.impl.core.client.configure
import org.tesserakt.diskordin.rest.integration.Ktor
import java.io.File

@KtorExperimentalAPI
@DiscordClientBuilderScope.InternalTestAPI
suspend fun main(args: Array<String>) {
    val file = File("cache.txt")

    val client = DiscordClientBuilder by Ktor(CIO) configure {
        +token(args[0])
        +gatewaySettings {
            +gatewayInterceptor(object : EventInterceptor() {
                override suspend fun Context.messageCreate(event: MessageCreateEvent) {
                    if (event.message().content.startsWith("!!~save cache")) {
                        FileCacheSnapshot.fromSnapshot(event.cacheSnapshot).writeTo(file.writer().buffered())
                        event.channel().createMessage("Saved!")
                    } else if (event.message().content.startsWith("!!~logout")) {
                        event.client.logout()
                    }
                }
            })
            +coroutineContext(Dispatchers.Unconfined)
        }
    }

    client.login()
}