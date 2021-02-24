package org.tesserakt.diskordin.commands.feature

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import org.tesserakt.diskordin.commands.ValidationError

data class AliasesFeature(
    val commandName: String,
    val aliases: List<String>
) : Feature<AliasesFeature> {
    data class DuplicatedAliases(val commandName: String, val duplicated: Iterable<String>) :
        ValidationError("Aliases(${duplicated.joinToString()}) duplicates with name or other aliases of command $commandName")

    override fun validate(): ValidatedNel<ValidationError, AliasesFeature> {
        val counted = aliases.associateBy({ it }, { aliases.count { i -> i == it } })
        val equalsToCommandName = aliases.any { it == commandName }

        return if (equalsToCommandName || counted.any { it.value > 1 })
            DuplicatedAliases(commandName, counted.filterValues { it > 1 }.keys).invalidNel()
        else this@AliasesFeature.validNel()
    }
}