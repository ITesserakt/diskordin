package org.tesserakt.diskordin.commands.resolver

import arrow.core.Either
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.StaticMention
import org.tesserakt.diskordin.core.entity.client

class UserResolver : MentionableTypeResolver<IUser, CommandContext> {
    override suspend fun parseByMention(context: CommandContext, input: String): IUser? {
        val (id) = static.mention.find(input)!!.destructured
        val snowflake = id.asSnowflake()

        return Either.catch { context.client.getUser(snowflake) }.orNull()
    }

    override suspend fun parseById(context: CommandContext, input: String): IUser? {
        val snowflake = input.asSnowflake()

        return Either.catch { context.client.getUser(snowflake) }.orNull()
    }

    override suspend fun rawParse(context: CommandContext, input: String): IUser? {
        val users = context.client.users
        val user = input.split('#')

        return if (user.size != 2 || user[1].toShortOrNull() != null)
            null
        else {
            val (name, discriminator) = user
            users.find { (it.username == name || it.name == name) && it.discriminator == discriminator.toShort() }
        }
    }

    override val static: StaticMention<IUser, *> = IUser.Companion
}