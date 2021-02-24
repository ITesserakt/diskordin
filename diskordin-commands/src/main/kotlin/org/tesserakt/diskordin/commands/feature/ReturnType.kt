package org.tesserakt.diskordin.commands.feature

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import org.tesserakt.diskordin.commands.ValidationError
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

data class ReturnType(
    val commandName: String,
    private val _returnType: KType
) : PersistentFeature<ReturnType> {
    data class InvalidReturnType(val commandName: String, val actual: KType) :
        ValidationError("Return type of $commandName doesn't match to Unit. Actual: $actual")

    data class UnresolvedType(val type: KType) :
        ValidationError("$type doesn't resolved. Try rescan including this class")

    val returnType = Unit::class

    override fun validate(): ValidatedNel<ValidationError, ReturnType> = when {
        _returnType.isMarkedNullable -> InvalidReturnType(commandName, _returnType).invalidNel()
        _returnType.classifier is KClass<*> && returnType.isSubclassOf(_returnType.classifier as KClass<*>) -> validNel()
        else -> UnresolvedType(_returnType).invalidNel()
    }
}
