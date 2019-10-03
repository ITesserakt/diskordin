package ru.tesserakt.diskordin.core.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpResponseData
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.util.date.GMTDate
import io.ktor.util.date.Month
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be`
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import ru.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import ru.tesserakt.diskordin.impl.core.client.PredefinedHttpClient
import ru.tesserakt.diskordin.rest.Routes
import kotlin.coroutines.EmptyCoroutineContext

class DiscordClientTest {
    private val httpCl = PredefinedHttpClient("", TokenType.Bot.name).get(MockEngine) {
        addHandler {
            when (it.url.fullPath) {
                "/api/v6" + Routes.getCurrentUser().urlTemplate -> HttpResponseData(
                    HttpStatusCode.OK,
                    GMTDate(1, 2, 3, 4, Month.AUGUST, 6),
                    Headers.Empty,
                    HttpProtocolVersion.HTTP_1_1,
                    "{\n" +
                            "  \"id\": \"80351110224678912\",\n" +
                            "  \"username\": \"Nelly\",\n" +
                            "  \"discriminator\": \"1337\",\n" +
                            "  \"avatar\": \"8342729096ea3675442027381ff50dfe\",\n" +
                            "  \"verified\": true,\n" +
                            "  \"email\": \"nelly@discordapp.com\",\n" +
                            "  \"flags\": 64,\n" +
                            "  \"premium_type\": 1\n" +
                            "}",
                    EmptyCoroutineContext
                )
                "/api/v6" + Routes.getUser(547489107585007636).urlTemplate -> HttpResponseData(
                    HttpStatusCode.OK,
                    GMTDate(1, 2, 3, 4, Month.AUGUST, 6),
                    Headers.Empty,
                    HttpProtocolVersion.HTTP_1_1,
                    "{\n" +
                            "  \"id\": \"80351110224678912\",\n" +
                            "  \"username\": \"Nelly\",\n" +
                            "  \"discriminator\": \"1337\",\n" +
                            "  \"avatar\": \"8342729096ea3675442027381ff50dfe\",\n" +
                            "  \"verified\": true,\n" +
                            "  \"email\": \"nelly@discordapp.com\",\n" +
                            "  \"flags\": 64,\n" +
                            "  \"premium_type\": 1\n" +
                            "  \"bot\": true\n" +
                            "}",
                    EmptyCoroutineContext
                )
                else -> throw NoSuchElementException()
            }
        }
    }

    val discordClient = DiscordClientBuilder {
        httpClient = httpCl
        token = "NTQ3NDg5MTA3NTg1MDA3NjM2.XQq07A.0POl52ji2E4lFlvf9HzdOw-Aisw"
    }

    @BeforeAll
    fun setup() = runBlocking {
        discordClient.login()
    }

    @Test
    fun `after init`() = runBlocking<Unit> {
        //discordClient.isConnected `should be` true
        discordClient.self().isBot `should be` true
    }

    @AfterAll
    fun teardown() {
        discordClient.logout()
    }
}