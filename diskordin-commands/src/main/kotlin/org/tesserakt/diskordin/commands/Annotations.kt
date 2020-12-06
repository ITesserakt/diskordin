@file:Suppress("unused")

package org.tesserakt.diskordin.commands

/**
 * This annotation used to show that this function is a command.
 *
 * If [name] is not specified then name of function used
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class Command(val name: String = "")

/**
 * Adds a description to a command
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Description(val description: String)

/**
 * Adds a set of aliases to a command
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Aliases(vararg val aliases: String)

/**
 * Hide a command from commands list, but it can be run from Discord by name
 *
 * May be applied to function to hide only one command or class to hide all commands in this class
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Hide

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Ignore