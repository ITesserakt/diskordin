package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.UserEditRequest

class UserEditBuilder : BuilderBase<UserEditRequest>() {
    lateinit var username: String
    lateinit var avatar: String

    override fun create(): UserEditRequest = UserEditRequest(
        username, avatar
    )
}
