package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.ValidationError

class HiddenFeature : Feature<HiddenFeature> {
    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, HiddenFeature> = AE.just(this)
}