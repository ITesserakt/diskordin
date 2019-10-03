package ru.tesserakt.diskordin.gateway.json

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import ru.tesserakt.diskordin.util.gson
import ru.tesserakt.diskordin.util.toJsonTree
import kotlin.reflect.KClass

data class Payload(
    @SerializedName("op") val opcode: Int,
    @SerializedName("s") val seq: Int?,
    @SerializedName("t") val name: String?,
    @SerializedName("d") val rawData: JsonElement?
) {
    fun <T : IRawEvent> unwrap(clazz: KClass<T>): T = gson.fromJson(rawData.toString(), clazz.java)
}

inline fun <reified T : IGatewayCommand> T.wrap(opcode: Int, name: String, seq: Int?) =
    Payload(opcode, seq, name, this.toJsonTree())

inline fun <reified T : IGatewayCommand> T.wrapWith(opcode: Opcode, seq: Int?) =
    wrap(opcode.ordinal, opcode.name, seq)

interface IPayload
interface IGatewayCommand : IPayload
interface IRawEvent : IPayload