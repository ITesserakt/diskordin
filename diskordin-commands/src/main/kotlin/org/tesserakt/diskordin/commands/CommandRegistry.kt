@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands

import arrow.typeclasses.Traverse
import org.tesserakt.diskordin.commands.feature.HiddenFeature

@RequiresOptIn("This statement should not be used. This is part of a private implementation")
annotation class PrivateStatement

abstract class CommandRegistry private constructor(private val allCommands: List<CommandObject>) : List<CommandObject> {
    override fun toString(): String =
        "CommandRegistry contains $size commands with ${allCommands.filter { it.hasFeature<HiddenFeature>() }.size} hidden ones"

    companion object {
        val EMPTY: CommandRegistry = object : CommandRegistry(emptyList()), List<CommandObject> by emptyList() {}

        @OptIn(PrivateStatement::class)
        operator fun <F> invoke(
            validator: CommandBuilder.Validator<F>,
            T: Traverse<F>,
            f: RegisterScope<F>.() -> Unit
        ): CommandRegistry {
            val allCommands = RegisterScope(validator, T).apply(f).commands
            val publicCommands = allCommands.filter { !it.hasFeature<HiddenFeature>() }
            return object : CommandRegistry(allCommands), List<CommandObject> by publicCommands {}
        }

        @OptIn(PrivateStatement::class)
        operator fun invoke(commands: List<CommandObject>): CommandRegistry {
            val publicCommands = commands.filter { !it.hasFeature<HiddenFeature>() }
            return object : CommandRegistry(commands), List<CommandObject> by publicCommands {}
        }
    }

    class RegisterScope<F>(private val validator: CommandBuilder.Validator<F>, private val T: Traverse<F>) {
        @PrivateStatement
        internal val commands: MutableList<CommandObject> = mutableListOf()

        @OptIn(PrivateStatement::class)
        operator fun CommandObject.unaryPlus() {
            commands += this
        }

        fun RegisterScope<F>.command(f: CommandBuilder.() -> Unit) =
            CommandBuilder().apply(f).validate(validator, T)

        fun RegisterScope<F>.command(value: CommandBuilder) = value.validate(validator, T)
    }
}