
import io.ktor.client.engine.cio.*
import io.ktor.util.*
import org.tesserakt.diskordin.core.cache.FileCacheSnapshot
import org.tesserakt.diskordin.core.client.InternalTestAPI
import org.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.entity.cacheSnapshot
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.configure
import org.tesserakt.diskordin.rest.integration.Ktor
import java.io.File

@KtorExperimentalAPI
@InternalTestAPI
suspend fun main() {
    val file = File("cache.json")

    val client = DiscordClientBuilder by Ktor(CIO) configure {
        +gatewaySettings {
            +compressShards()
            +featureOverrides(Intents.all)
            +gatewayInterceptor<TokenInterceptor.Context> { println(it.token) }
            +gatewayInterceptor(object : EventInterceptor() {
                override suspend fun Context.messageCreate(event: MessageCreateEvent) {
                    when (event.message().content) {
                        "!!~save cache" -> {
                            FileCacheSnapshot.fromSnapshot(event.cacheSnapshot).writeTo(file.writer().buffered())
                            event.channel().createMessage("Saved!")
                        }
                        "!!~logout" -> event.client.logout()
                        "!!~get locales" -> event.cacheSnapshot.users.values
                            .filter { it.isFullyLoaded }
                            .mapNotNull { it.locale }
                            .let { event.channel().createMessage(it.toString()) }
                        "!!~boom" -> error("test")
                    }
                }
            })
        }
    }

    client.login()
}