@file:Suppress("RedundantSuspendModifier")

package org.tesserakt.diskordin.impl.gateway.interpreter

import mu.KLogger
import mu.KotlinLogging

internal val logger = KotlinLogging.logger("[Web socket transactions]")

internal suspend fun KLogger.logSend(state: Boolean, opcodeOrName: String) = if (state)
    debug("--> SEND #$opcodeOrName")
else
    warn("--> NOT SEND #$opcodeOrName")