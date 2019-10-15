package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IIntegration
import ru.tesserakt.diskordin.impl.core.entity.Integration


data class AccountResponse(
    val id: Long,
    val name: String
) : DiscordResponse<IIntegration.IAccount>() {
    override fun unwrap(vararg params: Any): IIntegration.IAccount = Integration.Account(this)
}
