package org.tesserakt.diskordin.utl.enums

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import io.kotest.property.forAll
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IActivity
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.*
import org.tesserakt.diskordin.util.typeclass.Integral

class ValuedEnumLaws : FreeSpec({
    fun <E, N> buildContext(integral: Integral<N>) where E : Enum<E>, E : IValued<E, N>, N : Any =
        object : Hash<ValuedEnum<E, N>> by ValuedEnum.hash(), Monoid<ValuedEnum<E, N>> by ValuedEnum.monoid(integral) {}

    include("Permissions ", buildContext<Permission, Long>(Long.integral()).generalizedTests())
    include("Intents ", buildContext<Intents, Short>(Short.integral()).generalizedTests())
    include("User.Flags ", buildContext<IUser.Flags, Short>(Short.integral()).generalizedTests())
    include("Activity.Flags ", buildContext<IActivity.Flags, Short>(Short.integral()).generalizedTests())
})

private inline fun <reified E, N, CTX> CTX.generalizedTests()
        where E : Enum<E>, E : IValued<E, N>, N : Any, CTX : Hash<ValuedEnum<E, N>>, CTX : Monoid<ValuedEnum<E, N>> =
    freeSpec {
        val enumGen = Arb.enum<E>().flatMap { a ->
            Arb.enum<E>().flatMap { b ->
                Arb.enum<E>().flatMap { c ->
                    Arb.enum<E>().map { d -> a or b and c xor d }
                }
            }
        }

        "Eq laws" - {
            "Reflexivity (x == x === true)" {
                checkAll(enumGen) { x ->
                    x.eqv(x).shouldBeTrue()
                }
            }
            "Symmetry (x == y === y == x)" {
                checkAll(enumGen, enumGen) { x, y ->
                    x.eqv(y) shouldBe y.eqv(x)
                }
            }
            "Transitivity ((x == y && y == z) -> x == z)" {
                forAll(enumGen, enumGen, enumGen) { x, y, z ->
                    !(x.eqv(y) && y.eqv(z)) || x.eqv(z)
                }
            }
            "Typeclass and built-in identity" {
                checkAll(enumGen, enumGen) { x, y ->
                    x.eqv(y) shouldBe (x == y)
                }
            }
        }
        "Hash laws" - {
            "Value and hash identity (A == B -> hash(A) == hash(b))" {
                forAll(enumGen, enumGen) { x, y ->
                    !x.eqv(y) || (x.hash() == y.hash())
                }
            }
            "hash() should be pure" {
                checkAll(enumGen) { x ->
                    x.hash() shouldBe x.hash()
                }
            }
        }
        "Semigroup laws" - {
            "Associativity ((A + B) + C === A + (B + C))" {
                checkAll(enumGen, enumGen, enumGen) { a, b, c ->
                    ((a + b) + c) shouldBe (a + (b + c))
                }
            }
        }
        "Monoid laws" - {
            "Left identity (Empty + A === A)" {
                checkAll(enumGen) { a ->
                    empty() + a shouldBe a
                }
            }
            "Right identity (A + Empty === A)" {
                checkAll(enumGen) { a ->
                    a + empty() shouldBe a
                }
            }
            "Combine all on empty list should be empty" {
                emptyList<ValuedEnum<E, N>>().combineAll() shouldBe empty()
            }
        }
    }