package ru.tesserakt.diskordin.core.entity

interface IAttachment : IEntity {
    val fileName: String
    val url: String
}