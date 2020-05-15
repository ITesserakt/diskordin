package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.ValidationError
import kotlin.reflect.KProperty

data class ModuleFeature(
    val moduleType: Class<CommandModule<*, *>>,
    val instance: CommandModule<*, *>
) : PersistentFeature<ModuleFeature> {
    data class TypeNonEquality(val expected: Class<*>, val actual: Class<*>) :
        ValidationError("Expected ${expected.name} as module type but got ${actual.name}")

    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, ModuleFeature> = AE.run {
        if (moduleType != instance::class.java) raiseError(TypeNonEquality(moduleType, instance::class.java).nel())
        else just(this@ModuleFeature)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <F, C : CommandContext<F>, M : CommandModule<F, C>> getValue(thisRef: Nothing?, prop: KProperty<*>) =
        instance as M
}