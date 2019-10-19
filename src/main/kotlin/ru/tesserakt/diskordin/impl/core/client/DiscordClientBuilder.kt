package ru.tesserakt.diskordin.impl.core.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.context.loadKoinModules
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.gateway.GatewayLifecycle
import ru.tesserakt.diskordin.rest.service.*
import java.util.concurrent.atomic.AtomicBoolean

class DiscordClientBuilder private constructor() {
    var token: String = "Invalid"
    var tokenType: TokenType = TokenType.Bot
    var gatewayScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())

    companion object {
        private val isEnabled = AtomicBoolean(false)

        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)
            check(isEnabled.compareAndSet(false, true)) { "Discord client already started" }

            val koin = setupKoin()
            if (koin.getProperty<String>("token") == null)
                koin.setProperty("token", builder.token)
            koin.setProperty("gatewayScope", builder.gatewayScope)

            return DiscordClient(builder.tokenType).also { client ->
                loadKoinModules(module {
                    single { client } bind IDiscordClient::class
                    single { setupHttpClient(get()) }
                    single {
                        setupRetrofit(
                            koin.getProperty<String>("API_url")
                                ?: throw NullPointerException("There is no url to discord API. Please specify it in koin.properties file"),
                            get()
                        )
                    }
                    single { setupLifecycle() } bind GatewayLifecycle::class
                    single { (path: String) -> setupScarlet(path, get(), get()) }
                    single { get<Retrofit>().create<ChannelService>() }
                    single { get<Retrofit>().create<EmojiService>() }
                    single { get<Retrofit>().create<GatewayService>() }
                    single { get<Retrofit>().create<GuildService>() }
                    single { get<Retrofit>().create<InviteService>() }
                    single { get<Retrofit>().create<UserService>() }
                    single { get<Retrofit>().create<VoiceService>() }
                    single { get<Retrofit>().create<WebhookService>() }
                })
            }
        }
    }
}