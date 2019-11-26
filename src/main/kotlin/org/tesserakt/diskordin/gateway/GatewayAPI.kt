package org.tesserakt.diskordin.gateway

import arrow.Kind
import arrow.free.Free
import arrow.free.extensions.FreeMonad
import arrow.higherkind
import arrow.syntax.function.andThen
import arrow.syntax.function.partially1
import com.tinder.scarlet.websocket.WebSocketEvent
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

    companion object : FreeMonad<ForGatewayAPIF>
}

typealias GatewayAPI<A> = Free<ForGatewayAPIF, A>

fun heartbeat(data: Payload<Heartbeat>): GatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.HeartbeatPayload(data))

fun identify(data: Payload<Identify>): GatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.IdentifyPayload(data))

fun resume(data: Payload<Resume>): GatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.ResumePayload(data))

fun requestMembers(data: Payload<RequestGuildMembers>): GatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.RequestMembersPayload(data))

fun invalidateSession(data: Payload<InvalidSession>): GatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.InvalidSessionPayload(data))

fun updateVoiceState(data: Payload<UpdateVoiceState>): GatewayAPI<Boolean> =
    Free.liftF(GatewayAPIF.UpdateVoiceStatePayload(data))

fun sendPayload(data: GatewayCommand, lastSequenceId: Int?) = when (data) {
    is UpdateVoiceState -> data::wrapWith.partially1(Opcode.VOICE_STATUS_UPDATE) andThen ::updateVoiceState
    is RequestGuildMembers -> data::wrapWith.partially1(Opcode.REQUEST_GUILD_MEMBERS) andThen ::requestMembers
    is Resume -> data::wrapWith.partially1(Opcode.RESUME) andThen ::resume
    is Identify -> data::wrapWith.partially1(Opcode.IDENTIFY) andThen ::identify
    is InvalidSession -> data::wrapWith.partially1(Opcode.INVALID_SESSION) andThen ::invalidateSession
    is Heartbeat -> data::wrapWith.partially1(Opcode.HEARTBEAT) andThen ::heartbeat
}.invoke(lastSequenceId)

fun <F> observeWebSocketEvents(): GatewayAPI<Kind<F, WebSocketEvent>> = Free.liftF(GatewayAPIF.WebSocketEvents())
