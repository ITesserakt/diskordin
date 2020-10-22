package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
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

    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, ModuleInstanceFeature> = AE.run {
        if (!moduleType.isSubclassOf(instance::class)) raiseError(
            TypeNonEquality(moduleType, instance::class.java).nel()
        )
        else just(this@ModuleInstanceFeature)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <C : CommandContext, M : CommandModule<C>> getValue(thisRef: Nothing?, prop: KProperty<*>) =
        instance as M
}