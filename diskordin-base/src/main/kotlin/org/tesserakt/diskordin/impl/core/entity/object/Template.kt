package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.TemplateResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.ITemplate
import org.tesserakt.diskordin.core.entity.rest
import org.tesserakt.diskordin.rest.callCaching

class Template(raw: TemplateResponse) : ITemplate {
    override val code: String = raw.code
    override val description: String? = raw.description
    override val usageCount: Int = raw.usageCount
    override val creator: EagerIdentified<IUser> = raw.creatorId eager { raw.creator.unwrap() }
    override val createdAt = raw.createdAt
    override val updatedAt = raw.updatedAt
    override val sourceGuild: EagerIdentified<IGuild> = raw.sourceGuildId eager { raw.serializedSourceGuild.unwrap() }
    override val isSynced: Boolean = raw.isDirty ?: true
    override val name: String = raw.name

    override suspend fun unpackTemplate(): IGuild = rest.callCaching {
        templateService.createGuildFromTemplate(code)
    }
}