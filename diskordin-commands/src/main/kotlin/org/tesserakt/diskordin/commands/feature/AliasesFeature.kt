package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.ValidationError

data class AliasesFeature(
    val commandName: String,
    val aliases: List<String>
) : Feature<AliasesFeature> {
    data class DuplicatedAliases(val commandName: String, val duplicated: Iterable<String>) :
        ValidationError("Aliases(${duplicated.joinToString()}) duplicates with name or other aliases of command[$commandName]")

    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, AliasesFeature> = AE.run {
        val counted = aliases.associateBy({ it }, { aliases.count { i -> i == it } })
        val equalsToCommandName = aliases.any { it == commandName }

        if (equalsToCommandName || counted.any { it.value > 1 })
            raiseError(DuplicatedAliases(commandName, counted.filterValues { it > 1 }.keys).nel())
        else this@AliasesFeature.just()
    }
}