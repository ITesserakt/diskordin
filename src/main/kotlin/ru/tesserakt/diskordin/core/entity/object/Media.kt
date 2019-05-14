package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IDiscordObject

interface IMedia : IDiscordObject {
    val url: String?
    val height: Int?
    val width: Int?
}

interface IImage : IMedia
interface IVideo : IMedia