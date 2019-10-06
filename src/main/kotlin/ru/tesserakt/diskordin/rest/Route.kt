package ru.tesserakt.diskordin.rest

import com.github.kittinunf.fuel.core.Method
import kotlin.reflect.KClass

internal data class Route<T : Any>(
    val httpMethod: Method,
    val urlTemplate: String,
    val clazz: KClass<T>
) {
    companion object {
        inline fun <reified T : Any> get(urlTemplate: String) =
            Route(Method.GET, urlTemplate, T::class)

        inline fun <reified T : Any> put(urlTemplate: String) =
            Route(Method.PUT, urlTemplate, T::class)

        inline fun <reified T : Any> post(urlTemplate: String) =
            Route(Method.POST, urlTemplate, T::class)

        inline fun <reified T : Any> patch(urlTemplate: String) =
            Route(Method.PATCH, urlTemplate, T::class)

        inline fun <reified T : Any> delete(urlTemplate: String) =
            Route(Method.DELETE, urlTemplate, T::class)
    }

    fun newRequest() = Requester(this)
}