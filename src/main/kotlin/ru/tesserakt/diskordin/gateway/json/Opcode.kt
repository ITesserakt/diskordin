package ru.tesserakt.diskordin.gateway.json

import ru.tesserakt.diskordin.gateway.json.commands.Identify
import ru.tesserakt.diskordin.gateway.json.commands.InvalidSession
import ru.tesserakt.diskordin.gateway.json.commands.Resume
import ru.tesserakt.diskordin.gateway.json.events.HeartbeatACK
import ru.tesserakt.diskordin.gateway.json.events.Hello
import kotlin.reflect.KClass

enum class Opcode(val type: KClass<out IPayload>) {
    DISPATCH(IRawEvent::class),
    HEARTBEAT(Heartbeat::class),
    IDENTIFY(Identify::class),
    STATUS_UPDATE(IPayload::class), //TODO: Stub
    VOICE_STATUS_UPDATE(IPayload::class), //TODO: Stub
    VOICE_SERVER_PING(IPayload::class), //TODO: Stub
    RESUME(Resume::class),
    RECONNECT(Nothing::class),
    REQUEST_GUILD_MEMBERS(IPayload::class), //TODO: Stub
    INVALID_SESSION(InvalidSession::class),
    HELLO(Hello::class),
    HEARTBEAT_ACK(HeartbeatACK::class);
}