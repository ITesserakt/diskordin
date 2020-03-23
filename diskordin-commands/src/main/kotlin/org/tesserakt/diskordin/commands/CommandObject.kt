package org.tesserakt.diskordin.commands

data class CommandObject(
    val name: String,
    val requiredFeatures: Set<Feature<*>>
)

inline fun <reified F : Feature<F>> CommandObject.hasFeature() = requiredFeatures.filterIsInstance<F>().isNotEmpty()

inline fun <reified F : Feature<F>> CommandObject.getFeature() = requiredFeatures.filterIsInstance<F>().firstOrNull()