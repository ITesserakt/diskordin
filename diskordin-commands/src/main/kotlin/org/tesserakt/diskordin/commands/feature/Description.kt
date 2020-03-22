package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.Feature
import org.tesserakt.diskordin.commands.ValidationError

data class Description(
    val commandName: String,
    val value: String
) : Feature<Description> {
    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, Description> = AE.just(this)
}