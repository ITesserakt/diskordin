package org.tesserakt.diskordin.gateway.interceptor

import arrow.syntax.function.andThen
import arrow.syntax.function.partially1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KLogger
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.commands.*
import org.tesserakt.diskordin.gateway.json.wrapWith
import org.tesserakt.diskordin.util.toJson
import kotlin.reflect.KClass

interface Interceptor<T : Interceptor.Context> {
    abstract class Context(internal val implementation: Implementation)

    val selfContext: KClass<T>
    private val logger: KLogger
        get() = KotlinLogging.logger("[WebSocket transactions]")

    suspend fun intercept(context: T)

    suspend fun Context.sendPayload(data: GatewayCommand, sequenceId: Int?) = withContext(Dispatchers.IO) {
        when (data) {
            is UpdateVoiceState -> data::wrapWith.partially1(Opcode.VOICE_STATUS_UPDATE)
            is RequestGuildMembers -> data::wrapWith.partially1(Opcode.REQUEST_GUILD_MEMBERS)
            is Resume -> data::wrapWith.partially1(Opcode.RESUME)
            is Identify -> data::wrapWith.partially1(Opcode.IDENTIFY)
            is InvalidSession -> data::wrapWith.partially1(Opcode.INVALID_SESSION)
            is Heartbeat -> data::wrapWith.partially1(Opcode.HEARTBEAT)
        } andThen {
            implementation.send(it.toJson()).also { result ->
                if (result) logger.debug("---> SENT ${it.opcode()}")
                else logger.debug("-x-> ERROR WHILE SENDING ${it.opcode()}")
            }
        }
    }(sequenceId)
}