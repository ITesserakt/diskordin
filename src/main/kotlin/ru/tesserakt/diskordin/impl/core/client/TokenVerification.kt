package ru.tesserakt.diskordin.impl.core.client

import arrow.Kind
import arrow.typeclasses.MonadError
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.asSnowflakeSafe
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import java.util.*

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class TokenVerification<F>(
    private val token: String,
    private val tokenType: TokenType,
    ME: MonadError<F, VerificationError>
) : MonadError<F, VerificationError> by ME {
    fun verify(): Kind<F, Snowflake> {
        if (token.isBlank()) return raiseError(BlankString)
        if (token.contains(Regex("""\s"""))) return raiseError(InvalidCharacters)

        if (tokenType == TokenType.Webhook || tokenType == TokenType.Bearer) return token.asSnowflake().just()

        val parts = token.split('.')
        if (parts.size != 3) return raiseError(InvalidConstruction)

        return turnToSnowflake(parts[0])
    }

    private fun turnToSnowflake(tokenPart: String): Kind<F, Snowflake> = fx.monad {
        fun padBase64String(encoded: String): Kind<F, String> {
            if (encoded.indexOf('=') != -1) return encoded.just()
            val padding = (4 - (encoded.length % 4)) % 4
            if (padding == 3) return raiseError(CorruptedId)
            if (padding == 0) return encoded.just()
            return encoded.padEnd(encoded.length + padding, '=').just()
        }

        val padded = padBase64String(tokenPart).map {
            val bytes = Base64.getDecoder().decode(it)
            bytes.toString(Charsets.UTF_8)
        }.bind()

        padded.asSnowflakeSafe().fromEither {
            when (it) {
                is Snowflake.ConstructionError.NotANumber -> InvalidCharacters
                is Snowflake.ConstructionError.LessThenDiscordEpoch -> CorruptedId
            }
        }.bind()
    }

    internal sealed class VerificationError(val message: String) {
        object BlankString : VerificationError("Token cannot be blank")
        object InvalidCharacters : VerificationError("Token contains invalid characters!")
        object InvalidConstruction : VerificationError("Token does not fit into right form!")
        object CorruptedId : VerificationError("Token is corrupted!")
    }
}