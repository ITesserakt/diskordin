package org.tesserakt.diskordin.utl.enums

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.enum
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.Permission.*
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.*
import java.util.*

class ValuedEnumTest : StringSpec() {
    init {
        "Union and intersection of valued" {
            val sum = AddReactions and (!DeafenMembers xor BanMembers) or EmbedLinks
            //AddReactions intersects with all except DeafenMember and union with EmbedLinks
            sum should contain(AddReactions)
            sum shouldNot contain(DeafenMembers)
            sum should contain(EmbedLinks)
            sum.code shouldBe 16448
        }

        "Double invert should be equal to initial" {
            Arb.enum<Intents>()
                .generate(RandomSource.Default).take(20)
                .forAll {
                    (!!it.value).code shouldBe it.value.code
                }
        }

        "Code of valued should not change when converting" {
            val code = 2_146_958_847L //code with all Permissions, equals to Int.MAX_VALUE
            val valuedEnum = ValuedEnum<Permission, Long>(code, Long.integral())
            valuedEnum.asSet() shouldBe EnumSet.allOf(Permission::class.java)
        }
    }
}

private inline fun <reified E, I, T : ValuedEnum<E, I>> contain(other: IValued<E, I>): Matcher<T>
        where E : Enum<E>, E : IValued<E, I> = object : Matcher<T> {
    override fun test(value: T): MatcherResult = MatcherResult(value.contains(other),
        { "Enum should contain element(s) ${other.asSet()};" },
        { "Enum should not contain element(s) ${other.asSet()}" }
    )
}