package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IIntegration
import org.tesserakt.diskordin.impl.core.entity.Integration


data class AccountResponse(
    val id: Snowflake,
    val name: String
) : DiscordResponse<IIntegration.IAccount, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IIntegration.IAccount = Integration.Account(this)
}
