package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.ValidationError

data class DescriptionFeature(
    val commandName: String,
    val value: String
) : Feature<DescriptionFeature> {
    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, DescriptionFeature> =
        AE.just(this)
}