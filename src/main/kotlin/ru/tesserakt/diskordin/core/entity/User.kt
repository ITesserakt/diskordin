package ru.tesserakt.diskordin.core.entity

interface IUser : IMentioned, INamed {
    val username: String
    override val name: String
        get() = username
    val discriminator: Short
    val isBot: Boolean
}