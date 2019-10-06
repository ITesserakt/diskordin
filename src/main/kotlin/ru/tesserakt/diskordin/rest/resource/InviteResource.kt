@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.rest.Routes

internal object InviteResource {
    object General {

        suspend fun getInvite(code: String) =
            Routes.getInvite(code)
                .newRequest()
                .resolve()


        suspend fun deleteInvite(code: String, reason: String?) =
            Routes.deleteInvite(code)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()
    }
}