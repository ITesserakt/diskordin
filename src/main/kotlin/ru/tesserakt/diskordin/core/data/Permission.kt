package ru.tesserakt.diskordin.core.data

import java.util.*

enum class Permission {
    CreateInstantInvite {
        override val value: Long = 1
    },
    KickMembers {
        override val value: Long = 2
    },
    BanMembers {
        override val value: Long = 4
    },
    Administrator {
        override val value: Long = 8
    },
    ManageChannels {
        override val value: Long = 16
    },
    ManageGuild {
        override val value: Long = 32
    },
    AddReactions {
        override val value: Long = 64
    },
    ViewAuditLog {
        override val value: Long = 128
    },
    ViewChannels {
        override val value: Long = 1024
    },
    SendMessages {
        override val value: Long = 2048
    },
    SendTTSMessages {
        override val value: Long = 4096
    },
    ManageMessages {
        override val value: Long = 8192
    },
    EmbedLinks {
        override val value: Long = 16384
    },
    AttachFiles {
        override val value: Long = 32768
    },
    ReadMessageHistory {
        override val value: Long = 65536
    },
    MentionEveryone {
        override val value: Long = 131072
    },
    UseExternalEmojis {
        override val value: Long = 262144
    },
    Connect {
        override val value: Long = 1048576
    },
    Speak {
        override val value: Long = 2097152
    },
    MuteMembers {
        override val value: Long = 4194304
    },
    DeafenMembers {
        override val value: Long = 8388608
    },
    MoveMembers {
        override val value: Long = 16777216
    },
    UseVAD {
        override val value: Long = 33554432
    },
    PrioritySpeaker {
        override val value: Long = 256
    },
    ChangeNickname {
        override val value: Long = 67108864
    },
    ManageNicknames {
        override val value: Long = 134217728
    },
    ManageRoles {
        override val value: Long = 268435456
    },
    ManageWebhooks {
        override val value: Long = 536870912
    },
    ManageEmojis {
        override val value: Long = 1073741824
    };

    abstract val value: Long

    companion object {
        fun of(value: Long) = values().find { it.value == value }
    }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
operator fun EnumSet<Permission>.contains(element: Permission) =
    element.value and rawValue > 0

fun EnumSet<Permission>.computeCode() = this
    .map { it.value }
    .reduce { acc, code -> acc or code }

inline val EnumSet<Permission>.rawValue: Long
    inline get() = computeCode()

fun Long.computePermissions(): EnumSet<Permission> {
    val all = EnumSet.allOf(Permission::class.java)
    all.removeIf { it.value and this <= 0 }
    return all
}