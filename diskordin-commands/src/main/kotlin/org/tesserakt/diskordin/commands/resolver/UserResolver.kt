package org.tesserakt.diskordin.commands.resolver

import arrow.Kind
import arrow.fx.handleError
import arrow.fx.typeclasses.Async
import arrow.typeclasses.Functor
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.util.fromIO
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.StaticMention
import org.tesserakt.diskordin.core.entity.client

class UserResolver<F>(private val A: Async<F>) : MentionableTypeResolver<IUser, F, CommandContext<F>>, Functor<F> by A {
    override fun parseByMention(context: CommandContext<F>, input: String) = A.fx.async {
        val (id) = static.mention.find(input)!!.destructured
        val snowflake = id.asSnowflake()

        !context.client.getUser(snowflake).handleError { null }.fromIO(A)
    }

    override fun parseById(context: CommandContext<F>, input: String) = A.fx.async {
        val snowflake = input.asSnowflake()

        !context.client.getUser(snowflake).handleError { null }.fromIO(A)
    }

    override fun rawParse(context: CommandContext<F>, input: String): Kind<F, IUser?> {
        val users = context.client.users
        val user = input.split('#')

        return if (user.size != 2 || user[1].toShortOrNull() != null)
            A.just(null)
        else {
            val (name, discriminator) = user
            A.just(users.find { (it.username == name || it.name == name) && it.discriminator == discriminator.toShort() })
        }
    }

    override val static: StaticMention<IUser, *> = IUser.Companion
}