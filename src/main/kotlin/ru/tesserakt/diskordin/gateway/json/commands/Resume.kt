package ru.tesserakt.diskordin.gateway.json.commands

import ru.tesserakt.diskordin.gateway.json.IGatewayCommand

data class Resume(
    val token: String,
    val sessionId: String,
    val seq: Int
) : IGatewayCommand