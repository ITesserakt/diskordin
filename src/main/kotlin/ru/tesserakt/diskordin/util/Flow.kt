package ru.tesserakt.diskordin.util

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.flow

fun <T> ReceiveChannel<T>.receiveAsFlow() = flow {
    for (element in this@receiveAsFlow)
        this.emit(element)
}