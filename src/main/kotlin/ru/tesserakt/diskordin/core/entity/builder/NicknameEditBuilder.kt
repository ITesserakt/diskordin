package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.NicknameEditRequest

class NicknameEditBuilder : BuilderBase<NicknameEditRequest>() {
    lateinit var nickname: String

    override fun create(): NicknameEditRequest = NicknameEditRequest(
        nickname
    )
}
