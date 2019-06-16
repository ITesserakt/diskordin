package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.GroupDMCreateRequest

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