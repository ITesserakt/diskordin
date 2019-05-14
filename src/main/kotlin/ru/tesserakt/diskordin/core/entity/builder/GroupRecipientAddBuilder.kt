package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.GroupRecipientAddRequest

class GroupRecipientAddBuilder : IBuilder<GroupRecipientAddRequest> {
    lateinit var accessToken: String
    lateinit var nick: String

    override fun create(): GroupRecipientAddRequest = GroupRecipientAddRequest(
        accessToken,
        nick
    )
}