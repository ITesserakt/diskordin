@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands

import org.tesserakt.diskordin.core.entity.builder.Name

class CommandBuilder {
    private var name: String = ""
    private var description: String = ""
    private val aliases: MutableList<String> = mutableListOf()
    private var isHidden: Boolean = false
    private val features: MutableList<Feature> = mutableListOf()

    operator fun Name.unaryPlus() {
        name = this.v
    }

    operator fun String.unaryPlus() {
        description = this
    }

    @JvmName("unaryPlus_aliases")
    operator fun List<String>.unaryPlus() {
        aliases += this
    }

    operator fun Unit.unaryPlus() {
        isHidden = true
    }

    operator fun List<Feature>.unaryPlus() {
        features += this
    }

    inline fun CommandBuilder.name(value: String) = Name(value)
    inline fun CommandBuilder.description(value: String) = value
    inline fun CommandBuilder.aliases(vararg values: String) = values.toList()
    inline fun CommandBuilder.hide() = Unit
    inline fun CommandBuilder.features(vararg values: Feature) = values.toList()

    fun create() = CommandObject(
        name, description, aliases, isHidden, features
    )
}

data class CommandObject(
    val name: String,
    val description: String,
    val aliases: List<String>,
    val isHidden: Boolean,
    val requiredFeatures: List<Feature>
)