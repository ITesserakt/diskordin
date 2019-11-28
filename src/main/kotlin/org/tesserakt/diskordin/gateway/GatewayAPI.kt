package org.tesserakt.diskordin.gateway

import arrow.free.FreeApplicative
import arrow.free.extensions.FreeApplicativeApplicative
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
    data class Send(val data: Payload<out GatewayCommand>) : GatewayAPIF<Boolean>()
    object WebSocketEvents : GatewayAPIF<WebSocketEvent>()

    companion object : FreeApplicativeApplicative<ForGatewayAPIF>
}

typealias GatewayAPI<A> = FreeApplicative<ForGatewayAPIF, A>

fun sendPayload(data: GatewayCommand, lastSequenceId: Int?) = when (data) {
    is UpdateVoiceState -> data::wrapWith.partially1(Opcode.VOICE_STATUS_UPDATE)
    is RequestGuildMembers -> data::wrapWith.partially1(Opcode.REQUEST_GUILD_MEMBERS)
    is Resume -> data::wrapWith.partially1(Opcode.RESUME)
    is Identify -> data::wrapWith.partially1(Opcode.IDENTIFY)
    is InvalidSession -> data::wrapWith.partially1(Opcode.INVALID_SESSION)
    is Heartbeat -> data::wrapWith.partially1(Opcode.HEARTBEAT)
}.andThen { FreeApplicative.liftF(GatewayAPIF.Send(it)) }.invoke(lastSequenceId)

fun observeWebSocketEvents(): GatewayAPI<WebSocketEvent> =
    FreeApplicative.liftF(GatewayAPIF.WebSocketEvents)
