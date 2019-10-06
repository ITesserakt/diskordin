package ru.tesserakt.diskordin.gateway

import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import ru.tesserakt.diskordin.gateway.json.Heartbeat
import ru.tesserakt.diskordin.gateway.json.IRawEvent
import ru.tesserakt.diskordin.gateway.json.Opcode
import ru.tesserakt.diskordin.gateway.json.Payload
import ru.tesserakt.diskordin.gateway.json.commands.Identify
import ru.tesserakt.diskordin.gateway.json.events.HeartbeatACK
import ru.tesserakt.diskordin.gateway.json.events.Hello
import ru.tesserakt.diskordin.gateway.json.events.Ready

interface GatewayAPI {
    @Send
    fun heartbeat(data: Payload<Heartbeat>): Boolean

    @Receive
    fun allPayloads(): Flow<Payload<IRawEvent>>

    @Send
    fun identify(data: Payload<Identify>): Boolean

    @Receive
    fun observeWebSocketEvents(): Flow<WebSocketEvent>
}

fun GatewayAPI.observeHello() = allPayloads().filter { it.opcode() == Opcode.HELLO }.map { it.unwrap<Hello>() }
fun GatewayAPI.observeHeartbeatACK() =
    allPayloads().filter { it.opcode() == Opcode.HEARTBEAT_ACK }.map { HeartbeatACK() }

fun GatewayAPI.observeReady() =
    allPayloads().filter { it.opcode() == Opcode.DISPATCH && it.name == "READY" }.map { it.unwrap<Ready>() }
