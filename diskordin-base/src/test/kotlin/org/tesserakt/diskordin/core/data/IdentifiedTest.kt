package org.tesserakt.diskordin.core.data

import arrow.core.Eval
import arrow.core.Nel
import io.kotest.assertions.arrow.nel.forAll
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.IMentioned
import kotlin.random.Random

private data class LazyIdentified<out E : IEntity>(val id: Snowflake, private val render: () -> E) : IIdentified<E> {
    operator fun invoke() = render()
}

private infix fun <E : IEntity> Snowflake.lazyEval(render: (Snowflake) -> Eval<E>) =
    LazyIdentified(this) { render(this).value() }

private data class ManyIdentified<E : IEntity>(val id: Snowflake, private val items: Nel<E>) : IIdentified<E> {
    init {
        items.forAll { it.id shouldBe id }
    }

    operator fun invoke() = items
}

private infix fun <E : IEntity> Snowflake.many(render: (Snowflake) -> Nel<E>) = ManyIdentified(this, render(this))

class IdentifiedTest : StringSpec() {
    private val snowflake: Snowflake = Random.nextLong(4194305, Long.MAX_VALUE).asSnowflake()
    private val entity: IEntity = object : IEntity {
        override val id: Snowflake = snowflake
    }
    private val superEntity: IMentioned = object : IMentioned {
        override val id: Snowflake = snowflake
        override val mention: String = "Super cool mention"
    }

    init {
        "org.tesserakt.diskordin.core.data.Identified should not evaluate inner value" {
            var sideEffect = false

            val identified = snowflake lazyEval {
                Eval.later {
                    sideEffect = true
                    entity
                }
            }

            sideEffect.shouldBeFalse()
            identified.id shouldBe snowflake
            identified().id shouldBe snowflake
            sideEffect.shouldBeTrue()
        }
    }
}