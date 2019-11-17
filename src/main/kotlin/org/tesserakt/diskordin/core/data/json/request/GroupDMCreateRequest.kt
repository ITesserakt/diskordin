package org.tesserakt.diskordin.core.data.json.request


data class GroupDMCreateRequest(
    val access_tokens: Array<String>,
    val nicks: Map<Long, String>
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupDMCreateRequest

        if (!access_tokens.contentEquals(other.access_tokens)) return false
        if (nicks != other.nicks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = access_tokens.contentHashCode()
        result = 31 * result + nicks.hashCode()
        return result
    }
}
