package org.tesserakt.diskordin.rest.service

import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.`object`.IInvite

class InviteServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : InviteService {
    override suspend fun getInvite(code: String): InviteResponse<IInvite> =
        ktor.get("$discordApiUrl/api/v6/invites/$code")

    override suspend fun deleteInvite(code: String): Unit = ktor.delete("$discordApiUrl/api/v6/invites/$code")
}