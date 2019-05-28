package ru.tesserakt.diskordin.impl.core.client

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import java.util.*

private typealias VerificationResult<T> = Validated<TokenVerification.VerificationError, T>

internal class TokenVerification(private val token: String, private val tokenType: TokenType) {
    fun verify(): VerificationResult<Snowflake> {
        if (token.isBlank()) return VerificationError.BlankString.invalid()
        if (token.contains(Regex("""\s"""))) return VerificationError.InvalidCharacters.invalid()

        if (tokenType == TokenType.Webhook || tokenType == TokenType.Bearer) return token.asSnowflake().valid()

        val parts = token.split('.')
        if (parts.size != 3) return VerificationError.InvalidConstruction.invalid()

        return turnToSnowflake(parts[0]).fold({ it.invalid() }, { it.valid() })
    }

    private fun turnToSnowflake(tokenPart: String): VerificationResult<Snowflake> {
        fun padBase64String(encoded: String): VerificationResult<String> {
            if (encoded.indexOf('=') != -1) return encoded.valid()
            val padding = (4 - (encoded.length % 4)) % 4
            if (padding == 3) return VerificationError.CorruptedId.invalid()
            if (padding == 0) return encoded.valid()
            return encoded.padEnd(encoded.length + padding, '=').valid()
        }

        return padBase64String(tokenPart).map {
            val bytes = Base64.getDecoder().decode(it)
            bytes.toString(kotlinx.io.charsets.Charset.forName("Utf-8"))
        }.map { it.asSnowflake() }
    }

    internal sealed class VerificationError {
        object BlankString : VerificationError()
        object InvalidCharacters : VerificationError()
        object InvalidConstruction : VerificationError()
        object CorruptedId : VerificationError()
    }
}