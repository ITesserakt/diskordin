package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.ValidationError

interface Feature<F : Feature<F>> {
    fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, F>
}

interface PersistentFeature<F : PersistentFeature<F>> : Feature<F>

interface ModuleFeature<F : ModuleFeature<F>> : PersistentFeature<F>