package org.tesserakt.diskordin.core.data.event

sealed class ClientStatus {
    data class Desktop(val status: UserStatus) : ClientStatus()
    data class Mobile(val status: UserStatus) : ClientStatus()
    data class Web(val status: UserStatus) : ClientStatus()
}