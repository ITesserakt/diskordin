package org.tesserakt.diskordin.commands.resolver

abstract class ParseError(val description: String)
abstract class ConversionError(input: String, type: String) :
    ParseError("Could not convert '$input' to $type")