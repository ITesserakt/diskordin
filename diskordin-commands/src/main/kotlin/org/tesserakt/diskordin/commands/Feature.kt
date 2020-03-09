package org.tesserakt.diskordin.commands

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError

interface Feature {
    val validator: Validator

    abstract class Validator {
        abstract fun <F> validate(AE: ApplicativeError<F, Nel<ValidationError>>): Kind<F, Feature>
    }
}
