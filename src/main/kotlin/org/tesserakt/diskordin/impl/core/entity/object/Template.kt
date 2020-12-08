package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.TemplateResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.ITemplate
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.rest

class Template(raw: TemplateResponse) : ITemplate {
    init {
        cache[raw.sourceGuildId] = raw.serializedSourceGuild.unwrap()
        cache[raw.creatorId] = raw.creator.unwrap()
    }

    override val code: String = raw.code
    override val description: String? = raw.description
    override val usageCount: Int = raw.usageCount
    override val creator: Identified<IUser> = raw.creatorId identifyId { raw.creator.unwrap() }
    override val createdAt = raw.createdAt
    override val updatedAt = raw.updatedAt
    override val sourceGuild: Identified<IGuild> = raw.sourceGuildId identifyId { raw.serializedSourceGuild.unwrap() }
    override val isSynced: Boolean = raw.isDirty ?: true
    override val name: String = raw.name

    override suspend fun unpackTemplate(): IGuild = rest.call {
        templateService.createGuildFromTemplate(code)
    }
}