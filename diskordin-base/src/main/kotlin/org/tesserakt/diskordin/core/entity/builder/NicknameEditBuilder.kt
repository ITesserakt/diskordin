package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.NicknameEditRequest

@RequestBuilder
class NicknameEditBuilder(val nickname: String) : BuilderBase<NicknameEditRequest>() {
    override fun create(): NicknameEditRequest = NicknameEditRequest(
        nickname
    )
}
