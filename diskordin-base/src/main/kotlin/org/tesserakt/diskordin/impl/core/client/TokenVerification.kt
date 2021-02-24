package org.tesserakt.diskordin.impl.core.client

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.asSnowflakeEither
import org.tesserakt.diskordin.impl.core.client.VerificationError.*
import org.tesserakt.diskordin.util.DomainError
import java.util.*

internal fun String.verify(): Either<VerificationError, Snowflake> {
    val token = this@verify
    if (token.isBlank()) return BlankString.left()
    if (token.contains(Regex("""\s"""))) return InvalidCharacters.left()

    val parts = token.split('.')
    if (parts.size != 3) return InvalidConstruction.left()

    return turnToSnowflake(parts[0])
}

private fun turnToSnowflake(tokenPart: String): Either<VerificationError, Snowflake> {
    val padded = padBase64String(tokenPart).map {
        val bytes = Base64.getDecoder().decode(it)
        bytes.toString(Charsets.UTF_8)
    }

    return padded.flatMap { it.asSnowflakeEither().mapLeft { CorruptedId } }
}

private fun padBase64String(encoded: String): Either<VerificationError, String> {
    if (encoded.indexOf('=') != -1) return encoded.right()
    val padding = (4 - (encoded.length % 4)) % 4
    if (padding == 3) return CorruptedId.left()
    if (padding == 0) return encoded.right()
    return encoded.padEnd(encoded.length + padding, '=').right()
}

internal sealed class VerificationError(val message: String) : DomainError() {
    object BlankString : VerificationError("Token cannot be blank")
    object InvalidCharacters : VerificationError("Token contains invalid characters!")
    object InvalidConstruction : VerificationError("Token does not fit into right form!")
    object CorruptedId : VerificationError("Token is corrupted!")
}