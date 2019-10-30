package ru.tesserakt.diskordin.gateway.json

enum class Opcode {
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

fun Int.asOpcode() = Opcode.values().first { it.ordinal == this }