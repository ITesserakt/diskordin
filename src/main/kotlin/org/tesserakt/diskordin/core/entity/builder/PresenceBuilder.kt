package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.core.data.json.response.ActivityResponse
import org.tesserakt.diskordin.core.entity.`object`.IActivity
import java.time.Instant

@Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
@RequestBuilder
class PresenceBuilder : BuilderBase<UserStatusUpdateRequest>() {
    private var idleSince: Instant? = null
    private var game: ActivityResponse? = null
    private var status: StatusType =
        StatusType.Online
    private var isAFK: Boolean = false

    enum class StatusType {
        Online, DND, Idle, Invisible, Offline
    }

    operator fun StatusType.unaryPlus() {
        status = this
        if (this == StatusType.Idle)
            isAFK = true
    }

    operator fun ActivityBuilder.unaryPlus() {
        this@PresenceBuilder.game = this.create()
    }

    operator fun Instant.unaryPlus() {
        idleSince = this
    }

    inline fun PresenceBuilder.idleSince(value: Instant) = value
    inline fun PresenceBuilder.idleSinceNow(): Instant = Instant.now()
    inline fun PresenceBuilder.status(type: StatusType) = type
    inline fun PresenceBuilder.game(name: String, type: IActivity.Type, builder: ActivityBuilder.() -> Unit = {}) =
        ActivityBuilder(name, type).apply(builder)

    @Suppress("DEPRECATION")
    @Deprecated("Full of bugs", level = DeprecationLevel.HIDDEN)
    fun PresenceBuilder.customStatus(name: String, emoji: String): ActivityBuilder = //FIXME
        game("Custom Status", IActivity.Type.Custom) {
            +emoji(emoji)
            +state(name)
        }

    override fun create(): UserStatusUpdateRequest =
        UserStatusUpdateRequest(
            idleSince?.toEpochMilli(),
            game,
            status.name.toLowerCase(),
            isAFK
        )
}