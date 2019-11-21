package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.IntegrationEditRequest
import kotlin.properties.Delegates

@RequestBuilder
class IntegrationEditBuilder : BuilderBase<IntegrationEditRequest>() {
    var expireBehavior by Delegates.notNull<Int>()
    var expireGracePeriod: Int by Delegates.notNull()
    var enableEmoticons: Boolean by Delegates.notNull()

    override fun create(): IntegrationEditRequest = IntegrationEditRequest(
        expireBehavior, expireGracePeriod, enableEmoticons
    )
}
