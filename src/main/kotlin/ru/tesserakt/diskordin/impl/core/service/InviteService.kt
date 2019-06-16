package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.impl.core.entity.`object`.Invite
import ru.tesserakt.diskordin.impl.core.rest.resource.InviteResource

internal object InviteService {
    suspend fun getInvite(code: String) =
        Invite(InviteResource.General.getInvite(code))

    suspend fun deleteInvite(code: String, reason: String?) =
        InviteResource.General.deleteInvite(code, reason)
}