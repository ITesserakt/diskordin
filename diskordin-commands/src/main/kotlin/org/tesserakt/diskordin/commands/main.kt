package org.tesserakt.diskordin.commands

import arrow.Kind
import arrow.core.Id
import arrow.core.extensions.id.comonad.comonad
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicativeError.applicativeError
import arrow.fx.extensions.io.async.async
import arrow.fx.typeclasses.seconds
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.feature.FunctionBody
import org.tesserakt.diskordin.commands.feature.ModuleFeature
import org.tesserakt.diskordin.commands.integration.CompilerOutput
import org.tesserakt.diskordin.commands.integration.commandRegistry
import org.tesserakt.diskordin.commands.integration.enableCommandFramework
import org.tesserakt.diskordin.commands.integration.unaryPlus
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import kotlin.system.measureTimeMillis

fun main() {
    val client = DiscordClientBuilder.default {
        +token("NDgxMTY1MDA0NjEyMTczODI0.XrwK7Q.jOE8ojLRZmvhfw2dyqvmNStDUwU")
        +enableCommandFramework {
            +outputType(CompilerOutput.Summary)
        }
        +gatewaySettings {
            +gatewayInterceptor(Handler())
            +compressShards()
        }
    }
    client.login()
}

class Handler : EventInterceptor<ForIO>(IO.applicativeError()) {
    private val logger = KotlinLogging.logger { }

    override fun Context.messageCreate(event: MessageCreateEvent): Kind<ForIO, Unit> = IO.fx {
        val registry = event.client.commandRegistry
        if (event.author == null ||
            event.author?.invoke(Id.comonad())?.isBot == true ||
            !event.message(Id.comonad()).content.startsWith('~') ||
            event.guild?.invoke()?.bind() == null
        ) return@fx

        val content = event.message(Id.comonad()).content.drop(1).split(' ')
        val context = object : GuildCommandContext<ForIO> {
            override val message: Identified<IMessage> = event.message
            override val author: Identified<IUser> = event.author!!
            override val commandArgs: Array<String> = content.drop(1).toTypedArray()
            override val guild: IdentifiedF<ForIO, IGuild> = event.guild!!
            override val channel: IdentifiedF<ForIO, ITextChannel> =
                event.channel.map { it.map { c -> c as ITextChannel } }
        }

        registry.filter { it.name == content.first() }.forEach { obj ->
            val body = obj.getPersistentFeature<FunctionBody>()
            val module: TestModule by obj.getPersistentFeature<ModuleFeature>()

            body(IO.async(), module, context).bind().fold({
                val message = "Error while executing command `${obj.name}`"
                context.channel().bind().createMessage("$message. Message: ``${it.localizedMessage}``").bind()
                logger.error(message, it)
            }, { Unit })
        }
        Unit
    }
}

class TestModule : CommandModule<ForIO, GuildCommandContext<ForIO>>(IO.async()) {
    @Command
    fun logout(ctx: GuildCommandContext<ForIO>) = IO.fx {
        ctx.client.logout()
    }

    @Command("cache")
    fun checkCaches(ctx: GuildCommandContext<ForIO>) = IO.fx {
        val cache = ctx.client
        println(cache)
        !ctx.reply(cache.toString())
    }

    @Command
    fun huge(ctx: GuildCommandContext<ForIO>) = IO.fx {
        !ctx.reply("Starting computation...")
        val time = measureTimeMillis { !sleep(10.seconds) }
        !ctx.reply("End computation... Lasts $time ms")
    }

    @Command
    fun boom(ctx: GuildCommandContext<ForIO>) = IO.fx {
        !ctx.reply("Аллах акбар!")
        throw Exception()
    }
}
