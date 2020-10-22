package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
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

    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, ReturnType> = AE.run {
        when {
            _returnType.isMarkedNullable -> raiseError(InvalidReturnType(commandName, _returnType).nel())
            _returnType.classifier is KClass<*> && returnType.isSubclassOf(_returnType.classifier as KClass<*>) -> this@ReturnType.just()
            else -> raiseError(UnresolvedType(_returnType).nel())
        }
    }
}
