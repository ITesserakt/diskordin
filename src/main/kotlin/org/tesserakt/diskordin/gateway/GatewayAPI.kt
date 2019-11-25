package org.tesserakt.diskordin.gateway

import arrow.Kind
import arrow.free.Free
import arrow.free.extensions.FreeMonad
import arrow.higherkind
import arrow.syntax.function.bind
import com.tinder.scarlet.websocket.WebSocketEvent
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.commands.*
import org.tesserakt.diskordin.gateway.json.wrapWith

@higherkind
sealed class GatewayAPIF<T> : GatewayAPIFOf<T> {
    data class HeartbeatPayload(val data: Payload<Heartbeat>) : GatewayAPIF<Boolean>()
    data class IdentifyPayload(val data: Payload<Identify>) : GatewayAPIF<Boolean>()
    data class ResumePayload(val data: Payload<Resume>) : GatewayAPIF<Boolean>()
    data class RequestMembersPayload(val data: Payload<RequestGuildMembers>) : GatewayAPIF<Boolean>()
    data class InvalidSessionPayload(val data: Payload<InvalidSession>) : GatewayAPIF<Boolean>()
    data class UpdateVoiceStatePayload(val data: Payload<UpdateVoiceState>) : GatewayAPIF<Boolean>()

    class WebSocketEvents<F> : GatewayAPIF<Kind<F, WebSocketEvent>>()
    class DiscordEvents<F> : GatewayAPIF<Kind<F, Payload<IRawEvent>>>()

    companion object : FreeMonad<ForGatewayAPIF>
}

typealias NewGatewayAPI<A> = Free<ForGatewayAPIF, A>

fun heartbeat(data: Payload<Heartbeat>): NewGatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.HeartbeatPayload(data))

fun identify(data: Payload<Identify>): NewGatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.IdentifyPayload(data))

fun resume(data: Payload<Resume>): NewGatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.ResumePayload(data))

fun requestMembers(data: Payload<RequestGuildMembers>): NewGatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.RequestMembersPayload(data))

fun invalidateSession(data: Payload<InvalidSession>): NewGatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.InvalidSessionPayload(data))

fun updateVoiceState(data: Payload<UpdateVoiceState>): NewGatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.UpdateVoiceStatePayload(data))

fun sendPayload(data: GatewayCommand, lastSequenceId: Int?) = when (data) {
    is UpdateVoiceState -> { id -> updateVoiceState(data.wrapWith(Opcode.VOICE_STATUS_UPDATE, id)) }
    is RequestGuildMembers -> { id -> requestMembers(data.wrapWith(Opcode.REQUEST_GUILD_MEMBERS, id)) }
    is Resume -> { id: Int? -> resume(data.wrapWith(Opcode.RESUME, id)) }
    is Identify -> { id -> identify(data.wrapWith(Opcode.IDENTIFY, id)) }
    is InvalidSession -> { id -> invalidateSession(data.wrapWith(Opcode.INVALID_SESSION, id)) }
    is Heartbeat -> { id -> heartbeat(data.wrapWith(Opcode.HEARTBEAT, id)) }
}.bind(lastSequenceId).invoke()

fun <F> observeWebSocketEvents(): NewGatewayAPI<Kind<F, WebSocketEvent>> = Free.liftF(GatewayAPIF.WebSocketEvents())
fun <F> observeDiscordEvents(): NewGatewayAPI<Kind<F, Payload<IRawEvent>>> = Free.liftF(GatewayAPIF.DiscordEvents())
