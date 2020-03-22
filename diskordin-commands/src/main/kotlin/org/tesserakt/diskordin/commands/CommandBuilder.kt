@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.extensions.list.traverse.traverse
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Traverse
import org.tesserakt.diskordin.core.entity.builder.Name

class CommandBuilder {
    private var name: String = ""
    private val aliases: MutableList<String> = mutableListOf()
    private var isHidden: Boolean = false
    private val features: MutableSet<Feature<*>> = mutableSetOf()

    operator fun Name.unaryPlus() {
        name = this.v
    }

    @JvmName("unaryPlus_aliases")
    operator fun List<String>.unaryPlus() {
        aliases += this
    }

    operator fun Unit.unaryPlus() {
        isHidden = true
    }

    operator fun Set<Feature<*>>.unaryPlus() {
        features += this
    }

    inline fun CommandBuilder.name(value: String) = Name(value)
    inline fun CommandBuilder.aliases(vararg values: String) = values.toList()
    inline fun CommandBuilder.hide() = Unit
    inline fun CommandBuilder.features(values: Set<Feature<*>>) = values

    fun <V : Validator<F>, F> validate(validator: V, T: Traverse<F>) = validator.run {
        mapN(
            name.validateName(),
            validateAliases(),
            validateFeatures(T)
        ) { (name, aliases, features) ->
            CommandObject(name, aliases, isHidden, features.toSet())
        }
    }

    sealed class Validator<F>(private val AE: ApplicativeError<F, Nel<ValidationError>>) :
        ApplicativeError<F, Nel<ValidationError>> by AE {
        fun String.validateName() =
            if (this.isBlank()) raiseError(ValidationError.BlankName.nel())
            else just(this)

        fun CommandBuilder.validateAliases(): Kind<F, List<String>> {
            val counted = aliases.associateBy({ it }, { aliases.count { i -> i == it } })
            return if (aliases.any { it == name } || counted.any { it.value > 1 })
                raiseError(ValidationError.DuplicatedAliases(name, counted.filter { it.value > 1 }.keys).nel())
            else just(aliases)
        }

        fun CommandBuilder.validateFeatures(T: Traverse<F>): Kind<F, ListK<Feature<*>>> = T.run {
            features.map { it.validate(AE) }.traverse(AE, ::identity).map { it.fix() }
        }

        object AccumulateErrors :
            Validator<ValidatedPartialOf<NonEmptyList<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))

        object FailFast : Validator<EitherPartialOf<NonEmptyList<ValidationError>>>(Either.applicativeError())

        companion object {
            fun <A> accumulateErrors(f: AccumulateErrors.() -> A) = AccumulateErrors.f()
            fun <A> failFast(f: FailFast.() -> A) = FailFast.f()
        }
    }
}

abstract class ValidationError(val description: String) {
    object BlankName : ValidationError("Some command doesn't have name")
    data class DuplicatedAliases(val commandName: String, val duplicated: Iterable<String>) :
        ValidationError("Aliases(${duplicated.joinToString()}) duplicates with name or other aliases of command[$commandName]")
}