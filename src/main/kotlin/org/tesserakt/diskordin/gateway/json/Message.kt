package org.tesserakt.diskordin.gateway.json

import java.io.ByteArrayOutputStream
import java.util.zip.InflaterInputStream

sealed class Message {
    data class Text(val message: String) : Message()
    data class Bytes(val message: ByteArray) : Message() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Bytes

            if (!message.contentEquals(other.message)) return false

            return true
        }

        override fun hashCode(): Int {
            return message.contentHashCode()
        }
    }

    fun decompress() = when (this) {
        is Text -> this.message
        is Bytes -> decompressFromZLib(this.message)
    }

    private fun decompressFromZLib(input: ByteArray): String {
        val inflater = InflaterInputStream(input.inputStream())
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(1024)

        while (true) {
            val len = inflater.read(buffer)
            if (len <= 0) break
            output.write(buffer, 0, len)
        }

        output.close()
        inflater.close()

        return output.toString(Charsets.UTF_8.name())
    }
}