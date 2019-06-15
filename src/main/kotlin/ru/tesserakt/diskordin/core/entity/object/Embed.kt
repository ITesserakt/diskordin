package ru.tesserakt.diskordin.core.entity.`object`

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import java.awt.Color
import java.time.Instant

interface IEmbed : IDiscordObject {
    val title: String?
    val type: String?
    val description: String?
    val url: String?
    val timestamp: Instant?
    val color: Color?
    val footer: IFooter?
    val image: IImage?
    val thumbnail: IImage?
    val video: IVideo?
    val provider: IProvider?
    val author: IAuthor?
    @ExperimentalCoroutinesApi
    val fields: Flow<IField>

    interface IField {
        val name: String
        val value: String
        val inline: Boolean?
    }

    interface IAuthor {
        val name: String?
        val url: String?
        val iconUrl: String?
    }

    interface IProvider {
        val name: String?
        val url: String?
    }

    interface IFooter {
        val text: String
        val iconUrl: String?
    }
}