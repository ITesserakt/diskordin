package org.tesserakt.diskordin.commands.feature

import arrow.core.ValidatedNel
import arrow.core.validNel
import org.tesserakt.diskordin.commands.ValidationError

data class DescriptionFeature(
    val commandName: String,
    val value: String
) : Feature<DescriptionFeature> {
    override fun validate(): ValidatedNel<ValidationError, DescriptionFeature> = validNel()
}