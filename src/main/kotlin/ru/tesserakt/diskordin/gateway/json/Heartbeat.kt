package ru.tesserakt.diskordin.gateway.json

data class Heartbeat(
    val value: Int?
) : IRawEvent, IGatewayCommand