package org.tesserakt.diskordin.commands.integration

import org.tesserakt.diskordin.commands.compiler.CompilerSummary

sealed class Logger {
    internal abstract val logs: List<String>
    internal abstract fun logSummary(summary: CompilerSummary)
    abstract fun log(message: String)
    fun log(message: () -> String) = log(message())
}

object QuietLogger : Logger() {
    override val logs: List<String> = emptyList()
    override fun log(message: String) = Unit
    override fun logSummary(summary: CompilerSummary) = Unit
}

class SummaryLogger : Logger() {
    override val logs = mutableListOf<String>()

    override fun log(message: String) = Unit
    override fun logSummary(summary: CompilerSummary) {
        logs += summary.toString()
    }
}

class VerboseLogger : Logger() {
    override val logs = mutableListOf<String>()

    override fun log(message: String) {
        logs += message
    }

    override fun logSummary(summary: CompilerSummary) {
        logs += summary.toString()
    }
}