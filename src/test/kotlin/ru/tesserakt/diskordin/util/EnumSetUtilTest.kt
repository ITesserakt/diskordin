package ru.tesserakt.diskordin.util

import org.junit.jupiter.api.Test
import ru.tesserakt.diskordin.core.data.Permission
import java.util.*
import kotlin.test.assertEquals

internal class EnumSetUtilTest {
    private val enum1: EnumSet<Permission> = EnumSet.range(Permission.Connect, Permission.MuteMembers)
    private val enum2: EnumSet<Permission> = EnumSet.of(Permission.MuteMembers, Permission.AddReactions)

    @Test
    fun `or should be the same with plus`() {
        assertEquals(enum1 or enum2, enum1 + enum2)
    }

    @Test
    fun `xor should be the same with minus`() {
        assertEquals(enum1 xor enum2, enum1 - enum2)
    }

    @Test
    fun `not should invert all`() {
        val fullSize = EnumSet.allOf(Permission::class.java)
        assertEquals(fullSize.size - 2, (!enum2).size)
    }
}