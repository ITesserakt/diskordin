package ru.tesserakt.diskordin.impl.core.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.context.loadKoinModules
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

class DiscordClientBuilder private constructor() {
    var token: String = "Invalid"
    var tokenType: TokenType = TokenType.Bot
    var gatewayContext: CoroutineContext = Dispatchers.Default + Job()

    companion object {
        private val isEnabled = AtomicBoolean(false)

        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)
            check(isEnabled.compareAndSet(false, true)) { "Discord client already started" }

            val koin = setupKoin()
            if (koin.getProperty<String>("token") == null)
                koin.setProperty("token", builder.token)
            koin.setProperty("gatewayContext", builder.gatewayContext)

            return DiscordClient(builder.tokenType).also { client ->
                setupFuel(client)
                loadKoinModules(module {
                    single { client } bind IDiscordClient::class
                    single { setupHttpClient() }
                    single { (path: String) -> setupScarlet(path, get()) }
                })
            }
        }
    }
}