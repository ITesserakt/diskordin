package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.INamed

interface IRegion : INamed {
    val id: String
    val isOptimal: Boolean
    val isVIP: Boolean
    val isDeprecated: Boolean
    val isCustom: Boolean
}