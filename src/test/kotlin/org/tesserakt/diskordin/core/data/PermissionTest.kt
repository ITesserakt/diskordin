package org.tesserakt.diskordin.core.data

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.core.data.Permission.*
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.*
import java.util.*

internal class PermissionTest {
    @Test
    fun `union and intersect of sets`() {
        val sum = AddReactions and !DeafenMembers or EmbedLinks
        //AddReactions intersects with all except DeafenMember and union with EmbedLinks
        sum shouldContain AddReactions
        sum shouldNotContain DeafenMembers
        sum shouldContain EmbedLinks
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
        val set: EnumSet<Permission> = ValuedEnum<Permission, Long>(code, Long.integral()).asSet()
        EnumSet.allOf(Permission::class.java) `should equal` set
    }
}

private infix fun <E, I> ValuedEnum<E, I>.shouldContain(data: IValued<E, I>)
        where E : Enum<E>, E : IValued<E, I> =
    (data in this) `should be` true

private infix fun <E, I> ValuedEnum<E, I>.shouldNotContain(data: IValued<E, I>)
        where E : Enum<E>, E : IValued<E, I> =
    (data in this) `should be` false