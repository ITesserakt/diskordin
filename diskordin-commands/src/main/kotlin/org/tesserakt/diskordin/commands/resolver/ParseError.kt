package org.tesserakt.diskordin.commands.resolver

import org.tesserakt.diskordin.util.DomainError

abstract class ParseError(val description: String) : DomainError()
abstract class ConversionError(input: String, type: String) :
    ParseError("Could not convert '$input' to $type")