package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.GroupRecipientAddRequest

class GroupRecipientAddBuilder : BuilderBase<GroupRecipientAddRequest>() {
    lateinit var accessToken: String
    lateinit var nick: String

    override fun create(): GroupRecipientAddRequest = GroupRecipientAddRequest(
        accessToken,
        nick
    )
}