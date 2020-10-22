package org.tesserakt.diskordin.impl.core.client

import arrow.Kind
import arrow.core.Either
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.fix
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.asSnowflakeSafe
import org.tesserakt.diskordin.impl.core.client.VerificationError.*
import org.tesserakt.diskordin.util.DomainError
import java.util.*

internal fun <F> String.verify(ME: MonadError<F, VerificationError>): Kind<F, Snowflake> = ME.run {
    val token = this@verify
    if (token.isBlank()) return raiseError(BlankString)
    if (token.contains(Regex("""\s"""))) return raiseError(InvalidCharacters)

    val parts = token.split('.')
    if (parts.size != 3) return raiseError(InvalidConstruction)

    return turnToSnowflake(parts[0], ME)
}

private fun <F> turnToSnowflake(tokenPart: String, ME: MonadError<F, VerificationError>): Kind<F, Snowflake> = ME.run {
    val padded = padBase64String(tokenPart, ME).map {
        val bytes = Base64.getDecoder().decode(it)
        bytes.toString(Charsets.UTF_8)
    }

    padded.map { it.asSnowflakeSafe(Either.applicativeError()).fix() }.flatMap { either ->
        either.fromEither { CorruptedId }
    }
}

private fun <F> padBase64String(encoded: String, AE: ApplicativeError<F, VerificationError>): Kind<F, String> = AE.run {
    if (encoded.indexOf('=') != -1) return encoded.just()
    val padding = (4 - (encoded.length % 4)) % 4
    if (padding == 3) return raiseError(CorruptedId)
    if (padding == 0) return encoded.just()
    return encoded.padEnd(encoded.length + padding, '=').just()
}

internal sealed class VerificationError(val message: String) : DomainError() {
    object BlankString : VerificationError("Token cannot be blank")
    object InvalidCharacters : VerificationError("Token contains invalid characters!")
    object InvalidConstruction : VerificationError("Token does not fit into right form!")
    object CorruptedId : VerificationError("Token is corrupted!")
}