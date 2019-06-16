package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.IntegrationEditRequest
import kotlin.properties.Delegates

class IntegrationEditBuilder : BuilderBase<IntegrationEditRequest>() {
    var expireBehavior by Delegates.notNull<Int>()
    var expireGracePeriod: Int? = null
    var enableEmoticons: Boolean? = null

    override fun create(): IntegrationEditRequest = IntegrationEditRequest(
        expireBehavior, expireGracePeriod, enableEmoticons
    )
}
