package ru.tesserakt.diskordin.core.data

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import ru.tesserakt.diskordin.core.data.Permission.*
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import ru.tesserakt.diskordin.util.enums.and
import ru.tesserakt.diskordin.util.enums.asSet
import ru.tesserakt.diskordin.util.enums.not
import java.util.*

internal class PermissionTest {
    @Test
    fun `union and intersect of sets`() {
        val sum = AddReactions and !DeafenMembers or EmbedLinks
        //AddReactions intersects with all except DeafenMember and union with EmbedLinks
        (AddReactions in sum) `should be` true
        (DeafenMembers !in sum) `should be` true
        (EmbedLinks in sum) `should be` true
        sum.code `should be equal to` 16448
    }

    @Test
    fun `double invert should be equal to the initial state`() {
        for (entry in values())
            EnumSet.of(entry) `should equal` (!!entry).asSet()
    }

    @Test
    fun `code before compute and after recompute should be same`() {
        val code = 2146958847L //code with all entries
        val set = ValuedEnum<Permission>(code).asSet()
        EnumSet.allOf(Permission::class.java) `should equal` set
    }
}