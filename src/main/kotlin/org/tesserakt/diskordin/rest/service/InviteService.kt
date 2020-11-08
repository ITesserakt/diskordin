package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.`object`.IInvite

interface InviteService {
    suspend fun getInvite(code: String): InviteResponse<IInvite>

    suspend fun deleteInvite(code: String)
}