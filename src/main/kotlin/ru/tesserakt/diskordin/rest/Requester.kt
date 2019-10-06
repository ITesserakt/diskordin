package ru.tesserakt.diskordin.rest


import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.google.gson.Gson
import kotlinx.coroutines.delay
import org.koin.core.KoinComponent
import org.slf4j.Logger
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.toJson
import java.io.Reader
import kotlin.reflect.KClass

private const val MAX_RETRIES = 5
private const val RETRY_AFTER = 1000L

internal class Requester<T : Any>(private val route: Route<T>) : KoinComponent {
    private val logger: Logger by Loggers
    private var retryCount = 1

    private lateinit var headersInit: Map<String, Any>
    private var paramsInit: List<Pair<String, *>>? = null

    @Suppress("UNCHECKED_CAST")
    fun additionalHeaders(vararg pairs: Pair<String, *>) = apply {
        headersInit = (pairs.filterNot { it.second == null } as List<Pair<String, Any>>).toMap()
    }

    fun queryParams(init: List<Pair<String, *>>) = apply {
        paramsInit = init
    }

    @UseExperimental(ExperimentalStdlibApi::class)
    internal suspend fun resolve(
        body: Any? = null
    ): T = Fuel
        .request(route.httpMethod, route.urlTemplate, paramsInit)
        .also {
            if (::headersInit.isInitialized) it.header(headersInit)
            if (body != null) it.body(body.toJson())
        }.awaitObjectResult(gsonDeserializerOf(route.clazz))
        .fold({
            logger.debug("Successfully called to ${route.urlTemplate}")
            it
        }, {
            logger.error(
                "Crashed after call to ${it.response.url} with " +
                        "${it.localizedMessage} code (${it.errorData.decodeToString()})"
            )

            if (retryCount <= MAX_RETRIES) {
                delay(RETRY_AFTER)
                logger.warn("Retry #${retryCount++}...")
                return@fold resolve(body)
            }

            retryCount = 1
            throw IllegalStateException("Response doesn`t received after $MAX_RETRIES retries")
        })
}

internal fun <T : Any> gsonDeserializerOf(clazz: KClass<T>, gson: Gson = Gson()) = object : ResponseDeserializable<T> {
    override fun deserialize(reader: Reader): T? = gson.fromJson<T>(reader, clazz.java)
}