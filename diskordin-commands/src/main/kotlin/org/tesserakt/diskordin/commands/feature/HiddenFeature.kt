package org.tesserakt.diskordin.commands.feature

import arrow.core.ValidatedNel
import arrow.core.validNel
import org.tesserakt.diskordin.commands.ValidationError

class HiddenFeature : Feature<HiddenFeature> {
    override fun validate(): ValidatedNel<ValidationError, HiddenFeature> = validNel()
}