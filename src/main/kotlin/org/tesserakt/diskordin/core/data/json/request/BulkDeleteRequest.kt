package org.tesserakt.diskordin.core.data.json.request


data class BulkDeleteRequest(
    val messages: Array<Long>
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BulkDeleteRequest

        if (!messages.contentEquals(other.messages)) return false

        return true
    }

    override fun hashCode(): Int {
        return messages.contentHashCode()
    }
}
