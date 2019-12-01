package org.tesserakt.diskordin.gateway.json

enum class Opcode {
    UNDERLYING,
    DISPATCH,
    HEARTBEAT,
    IDENTIFY,
    STATUS_UPDATE,
    VOICE_STATUS_UPDATE,
    VOICE_SERVER_PING,
    RESUME,
    RECONNECT,
    REQUEST_GUILD_MEMBERS,
    INVALID_SESSION,
    HELLO,
    HEARTBEAT_ACK;
}

fun Opcode.asInt() = ordinal - 1

fun Int.asOpcode() = Opcode.values().first { it.asInt() == this }