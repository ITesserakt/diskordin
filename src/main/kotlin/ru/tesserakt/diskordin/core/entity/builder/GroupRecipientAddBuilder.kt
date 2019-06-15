package ru.tesserakt.diskordin.core.entity.builder

class GroupRecipientAddBuilder : BuilderBase<GroupRecipientAddRequest>() {
    lateinit var accessToken: String
    lateinit var nick: String

    override fun create(): GroupRecipientAddRequest = GroupRecipientAddRequest(
        accessToken,
        nick
    )
}