@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands

import arrow.core.*
import arrow.typeclasses.Semigroup
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.core.entity.builder.Name

class CommandBuilder {
    private var name: String = ""
    private val features: MutableSet<Feature<*>> = mutableSetOf()

    operator fun Name.unaryPlus() {
        name = this.v
    }

    operator fun Set<Feature<*>>.unaryPlus() {
        features += this
    }

    inline fun CommandBuilder.name(value: String) = Name(value)
    inline fun CommandBuilder.features(values: Set<Feature<*>>) = values

    fun validate() = Validator.run {
        Validated.mapN(Semigroup.nonEmptyList(), name.validateName(), validateFeatures(), ::CommandObject)
    }

    object Validator {
        @JvmStatic
        fun String.validateName() =
            if (this.isBlank()) ValidationError.BlankName.invalidNel()
            else this.validNel()

        @JvmStatic
        fun CommandBuilder.validateFeatures() =
            features.map { it.validate() }.sequenceValidated(Semigroup.nonEmptyList()).map { it.toSet() }
    }
}

abstract class ValidationError(val description: String) {
    object BlankName : ValidationError("Some command doesn't have name")
}