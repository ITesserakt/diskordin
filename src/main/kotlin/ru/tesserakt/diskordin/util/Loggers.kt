package ru.tesserakt.diskordin.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

object Loggers {
    operator fun <T : Any> getValue(thisRef: T, property: KProperty<*>): Logger {
        val thisClass = thisRef::class
        if (thisClass.isCompanion)
            return LoggerFactory.getLogger(thisClass.java.declaringClass)
        return LoggerFactory.getLogger(thisClass.java)
    }

    operator fun invoke(name: String): Logger = LoggerFactory.getLogger(name)
}