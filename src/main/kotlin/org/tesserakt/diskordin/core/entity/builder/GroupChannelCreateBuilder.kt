package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.GroupDMCreateRequest

@RequestBuilder
class GroupChannelCreateBuilder : BuilderBase<GroupDMCreateRequest>() {
    lateinit var accessTokens: Array<String>
    lateinit var users: Map<Snowflake, String>

    override fun create(): GroupDMCreateRequest = GroupDMCreateRequest(
        accessTokens,
        users.mapKeys {
            it.key.asLong()
        }
    )
}