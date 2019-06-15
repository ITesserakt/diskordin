package ru.tesserakt.diskordin.core.entity.builder

class NicknameEditBuilder : BuilderBase<NicknameEditRequest>() {
    lateinit var nickname: String

    override fun create(): NicknameEditRequest = NicknameEditRequest(
        nickname
    )
}
