@file:Suppress("unused")

package ru.tesserakt.diskordin.core.rest.service

import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.rest.Routes
import ru.tesserakt.diskordin.util.append

internal object InviteService {
    object General {

        suspend fun getInvite(code: String) =
            Routes.getInvite(code)
                .newRequest()
                .resolve<InviteResponse>()


        suspend fun deleteInvite(code: String, reason: String?) =
            Routes.deleteInvite(code)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()
    }
}