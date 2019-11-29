package org.tesserakt.diskordin.gateway.json

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand
import org.tesserakt.diskordin.util.gson
import org.tesserakt.diskordin.util.toJsonTree

data class Payload<T : IPayload>(
    @SerializedName("op") val opcode: Int,
    @SerializedName("s") val seq: Int?,
    @SerializedName("t") val name: String?,
    @SerializedName("d") val rawData: JsonElement?
) {
    inline fun <reified E : T> unwrap(): E = gson.fromJson(rawData, E::class.java)
    inline fun <reified R> unwrapAsResponse(): R = gson.fromJson(rawData, R::class.java)
    fun opcode() = opcode.asOpcode()
}

fun <T : GatewayCommand> T.wrap(opcode: Int, name: String, seq: Int?) =
    Payload<T>(opcode, seq, name, this.toJsonTree())

fun <T : GatewayCommand> T.wrapWith(opcode: Opcode, seq: Int?) =
    wrap(opcode.asInt(), opcode.name, seq)

interface IPayload
interface IRawEvent : IPayload
interface IToken : IPayload