package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.UserEditRequest

@RequestBuilder
class UserEditBuilder : BuilderBase<UserEditRequest>() {
    lateinit var username: String
    lateinit var avatar: String

    override fun create(): UserEditRequest = UserEditRequest(
        username, avatar
    )
}
