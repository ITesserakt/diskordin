package org.tesserakt.diskordin.commands

import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.feature.PersistentFeature

data class CommandObject(
    val name: String,
    val requiredFeatures: Set<Feature<*>>
)

inline fun <reified F : Feature<F>> CommandObject.hasFeature() = requiredFeatures.filterIsInstance<F>().isNotEmpty()
inline fun <reified F : Feature<F>> CommandObject.getFeature() = requiredFeatures.filterIsInstance<F>().firstOrNull()

inline fun <reified F : PersistentFeature<F>> CommandObject.getPersistentFeature() =
    requiredFeatures.filterIsInstance<F>().first()