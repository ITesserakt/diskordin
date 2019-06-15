package ru.tesserakt.diskordin.core.entity.builder

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