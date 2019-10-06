package ru.tesserakt.diskordin.gateway.json

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import ru.tesserakt.diskordin.util.gson
import ru.tesserakt.diskordin.util.toJsonTree

data class Payload<T : IPayload>(
    @SerializedName("op") val opcode: Int,
    @SerializedName("s") val seq: Int?,
    @SerializedName("t") val name: String?,
    @SerializedName("d") val rawData: JsonElement?
) {
    inline fun <reified E : T> unwrap(): E = gson.fromJson(rawData, E::class.java)
    fun opcode() = opcode.asOpcode()
}

fun <T : IGatewayCommand> T.wrap(opcode: Int, name: String, seq: Int?) =
    Payload<T>(opcode, seq, name, this.toJsonTree())

fun <T : IGatewayCommand> T.wrapWith(opcode: Opcode, seq: Int?) =
    wrap(opcode.ordinal, opcode.name, seq)

interface IPayload
interface IGatewayCommand : IPayload
interface IRawEvent : IPayload