package org.tesserakt.diskordin.commands

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError

interface Feature<F : Feature<F>> {
    fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, F>
}
