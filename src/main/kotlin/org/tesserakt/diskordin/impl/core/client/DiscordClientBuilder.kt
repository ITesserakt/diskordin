package org.tesserakt.diskordin.impl.core.client

import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.context.loadKoinModules
import org.koin.dsl.bind
import org.koin.dsl.module
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.rest.RestClient
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var gatewayScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun CoroutineContext.unaryPlus() {
        gatewayScope = CoroutineScope(this)
    }

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.context(coroutineContext: CoroutineContext) = coroutineContext

    companion object {
        private val isEnabled = AtomicBoolean(false)

        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)
            check(isEnabled.compareAndSet(false, true)) { "Discord client already started" }

            val koin = setupKoin()
            if (koin.getProperty<String>("token") == null)
                koin.setProperty("token", builder.token)
            koin.setProperty("gatewayScope", builder.gatewayScope)

            return DiscordClient().also { client ->
                loadKoinModules(module {
                    single { client } bind IDiscordClient::class
                    single { client.setupHttpClient(get()) }
                    single {
                        setupRetrofit(
                            koin.getProperty<String>("API_url")
                                ?: throw NullPointerException("There is no url to discord API. Please specify it in koin.properties file"),
                            get()
                        )
                    }
                    single { (path: String) -> setupScarlet(path, get()) }
                    single { RestClient(get(), IO.async()) }
                })
            }
        }
    }
}