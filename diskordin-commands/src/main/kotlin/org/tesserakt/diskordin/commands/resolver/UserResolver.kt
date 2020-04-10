package org.tesserakt.diskordin.commands.resolver

import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import arrow.fx.handleError
import arrow.fx.typeclasses.Async
import arrow.mtl.OptionT
import arrow.mtl.extensions.fx
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.util.fromIO
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.asSnowflakeSafe
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.StaticMention
import org.tesserakt.diskordin.core.entity.client

class UserResolver<F>(private val A: Async<F>) : MentionableTypeResolver<IUser, F, CommandContext<F>>, Functor<F> by A {
    override fun parseByMention(context: CommandContext<F>, input: String) = A.fx.async {
        val (id) = static.mention.find(input)!!.destructured
        val snowflake = !id.asSnowflakeSafe(A as ApplicativeError<F, Snowflake.ConstructionError>)

        !context.client.getUser(snowflake).map(::Some).handleError { None }.fromIO(A)
    }.let(OptionT.Companion::invoke)

    override fun parseById(context: CommandContext<F>, input: String): OptionT<F, IUser> = A.fx.async {
        val snowflake = !input.asSnowflakeSafe(A as ApplicativeError<F, Snowflake.ConstructionError>)

        !context.client.getUser(snowflake).map(::Some).handleError { None }.fromIO(A)
    }.let(OptionT.Companion::invoke)

    override fun rawParse(context: CommandContext<F>, input: String): OptionT<F, IUser> = OptionT.fx(A) {
        val users = context.client.users
        val user = input.split('#')

        if (user.size != 2 || user[1].toShortOrNull() != null)
            OptionT.none(A).bind<IUser>()
        else {
            val (name, discriminator) = user
            !OptionT.fromOption(A, users
                .find { (it.username == name || it.name == name) && it.discriminator == discriminator.toShort() }
                .toOption())
        }
    }

    override val static: StaticMention<IUser, *> = IUser.Companion
}