package org.tesserakt.diskordin.rest.storage

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity

object GlobalEntityCache : MutableMap<Snowflake, IEntity> by mutableMapOf()