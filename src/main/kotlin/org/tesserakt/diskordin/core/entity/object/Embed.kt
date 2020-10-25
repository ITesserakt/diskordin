package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.entity.IDiscordObject
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
    val fields: List<IField>

    interface IField : IDiscordObject {
        val name: String
        val value: String
        val inline: Boolean?
    }

    interface IAuthor : IDiscordObject {
        val name: String?
        val url: String?
        val iconUrl: String?
    }

    interface IProvider : IDiscordObject {
        val name: String?
        val url: String?
    }

    interface IFooter : IDiscordObject {
        val text: String
        val iconUrl: String?
    }
}