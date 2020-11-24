package org.tesserakt.diskordin.util.enums

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.Permission.*
import org.tesserakt.diskordin.impl.util.typeclass.integral
import java.util.*

internal class EnumsTest {
    @Test
    fun `union and intersection of sets`() {
        val sum = AddReactions and (!DeafenMembers xor BanMembers) or EmbedLinks
        println(sum.asSet())
        //AddReactions intersects with all except DeafenMember and BanMembers and union with EmbedLinks
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

    @Test
    fun `check of all`() {
        val all = ValuedEnum.all<Permission, Long>(Long.integral())
        all.asSet() `should equal` EnumSet.allOf(Permission::class.java)
    }

    @Test
    fun `check of none`() {
        val none = ValuedEnum.none<Permission, Long>(Long.integral())
        none.code `should be equal to` 0
        none.asSet() `should equal` EnumSet.noneOf(Permission::class.java)
    }

    @Test
    fun `check enhance`() {
        val set = EnumSet.of(AttachFiles, BanMembers)
        val codeForSet = 32768 + 4L
        set.enhance(Long.integral()) `should equal` ValuedEnum(codeForSet, Long.integral())
    }
}

private infix fun <E, I : Any> ValuedEnum<E, I>.shouldContain(data: IValued<E, I>)
        where E : Enum<E>, E : IValued<E, I> =
    (data in this) `should be` true

private infix fun <E, I : Any> ValuedEnum<E, I>.shouldNotContain(data: IValued<E, I>)
        where E : Enum<E>, E : IValued<E, I> =
    (data in this) `should be` false