package org.tesserakt.diskordin.core.entity

interface IGuildPreview : IEntity, INamed, IPreviewed<IGuild> {
    val icon: String?
    val splash: String?
    val discoverySplash: String?
    val emojis: List<IEmoji>
    val features: List<IGuild.Feature>
    val approximateMemberCount: Int
    val approximatePresenceCount: Int
    val description: String?
}