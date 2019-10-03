package ru.tesserakt.diskordin.gateway.json.commands

import ru.tesserakt.diskordin.gateway.json.IGatewayCommand

data class InvalidSession(
    val value: Boolean
) : IGatewayCommand
