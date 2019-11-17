package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.NicknameEditRequest

class NicknameEditBuilder : BuilderBase<NicknameEditRequest>() {
    lateinit var nickname: String

    override fun create(): NicknameEditRequest = NicknameEditRequest(
        nickname
    )
}
