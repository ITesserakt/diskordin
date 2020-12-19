package org.tesserakt.diskordin.util.enums

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IActivity
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.impl.util.typeclass.numeric
import org.tesserakt.diskordin.util.typeclass.Numeric
import java.util.*

class ValuedEnumLaws : FreeSpec({
    include("Permissions ", Long.numeric().testValuedEnum<Permission, Long>())
    include("Intents ", Short.numeric().testValuedEnum<Intents, Short>())
    include("User.Flags ", Int.numeric().testValuedEnum<IUser.Flags, Int>())
    include("Activity.Flags ", Short.numeric().testValuedEnum<IActivity.Flags, Short>())
})

fun <N> Numeric<N>.isPowerOf2(n: N): Boolean {
    tailrec fun f(init: N, input: N): Boolean = when {
        init > input -> false
        init < input -> f(init * 2.fromInt(), input)
        else -> true
    }

    return f(1.fromInt(), n)
}

inline fun <reified E, N : Any> Numeric<N>.testValuedEnum() where E : Enum<E>, E : IValued<E, N> = stringSpec {
    "All values should be power of two" {
        EnumSet.allOf(E::class.java)
            .map { it.code }.forAll { isPowerOf2(it).shouldBeTrue() }
    }
    "Sum of all codes should be less then MAX_VALUE" {
        EnumSet.allOf(E::class.java).map { it.code }
            .fold(zero) { acc, i -> acc + i }.toLong() shouldBeLessThanOrEqual maxValue.toLong()
    }
    "All values should be unique" {
        EnumSet.allOf(E::class.java).map { it.code }.shouldNotContainDuplicates()
    }
}