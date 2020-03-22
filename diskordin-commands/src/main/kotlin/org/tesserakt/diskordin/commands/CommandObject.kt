package org.tesserakt.diskordin.commands

data class CommandObject(
    val name: String,
    val aliases: List<String>,
    val isHidden: Boolean,
    val requiredFeatures: Set<Feature<*>>
)