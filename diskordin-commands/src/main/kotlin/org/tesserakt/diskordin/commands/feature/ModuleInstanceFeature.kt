package org.tesserakt.diskordin.commands.feature

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.ValidationError
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

data class ModuleInstanceFeature(
    val moduleType: KClass<CommandModule<*>>,
    val instance: CommandModule<*>
) : ModuleFeature<ModuleInstanceFeature> {
    data class TypeNonEquality(val expected: KClass<*>, val actual: Class<*>) :
        ValidationError("Expected ${expected.qualifiedName ?: "<local class>"} as module type but got ${actual.name}")

    override fun validate(): ValidatedNel<ValidationError, ModuleInstanceFeature> {
        return if (!moduleType.isSubclassOf(instance::class))
            TypeNonEquality(moduleType, instance::class.java).invalidNel()
        else validNel()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <C : CommandContext, M : CommandModule<C>> getValue(thisRef: Nothing?, prop: KProperty<*>) =
        instance as M
}