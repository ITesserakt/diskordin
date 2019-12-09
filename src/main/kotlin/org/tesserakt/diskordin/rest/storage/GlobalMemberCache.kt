package org.tesserakt.diskordin.rest.storage

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember

object GlobalMemberCache : MutableMap<Snowflake, IMember> by mutableMapOf()