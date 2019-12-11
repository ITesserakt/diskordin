package org.tesserakt.diskordin.impl.core.client

import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.rest.RestClient
import kotlin.coroutines.CoroutineContext

@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var gatewayContext: CoroutineContext = Dispatchers.IO + Job()

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun CoroutineContext.unaryPlus() {
        gatewayContext = this
    }

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.context(coroutineContext: CoroutineContext) = coroutineContext

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)

            stopKoin()
            val koin = setupKoin()
            if (koin.getProperty<String>("token") == null)
                koin.setProperty("token", builder.token)
            koin.setProperty("gatewayContext", builder.gatewayContext)

            return DiscordClient().unsafeRunSync().also { client ->
                loadKoinModules(module {
                    single { client.setupHttpClient(client) }
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