@file:Suppress("unused", "FunctionName")

package org.tesserakt.diskordin.core.data

import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.typeclass.Integral
import org.tesserakt.diskordin.util.typeclass.integral

enum class Permission(override val value: Long) : IValued<Permission, Long>, Integral<Long> by Long.integral() {
    CreateInstantInvite(1),
    KickMembers(2),
    BanMembers(4),
    ManageChannels(16),
    ManageGuild(32),
    AddReactions(64),
    ViewAuditLog(128),
    PrioritySpeaker(256),
    ViewChannels(1024),
    SendMessages(2048),
    SendTTSMessages(4096),
    ManageMessages(8192),
    EmbedLinks(16384),
    AttachFiles(32768),
    ReadMessageHistory(65536),
    MentionEveryone(131072),
    UseExternalEmojis(262144),
    Connect(1048576),
    Speak(2097152),
    MuteMembers(4194304),
    DeafenMembers(8388608),
    MoveMembers(16777216),
    UseVAD(33554432),
    ChangeNickname(67108864),
    ManageNicknames(134217728),
    ManageRoles(268435456),
    ManageWebhooks(536870912),
    ManageEmojis(1073741824),
    Administrator(8);
}