package org.tesserakt.diskordin.core.data

import arrow.core.Eval
import arrow.core.Id
import arrow.core.Nel
import arrow.core.extensions.eval.comonad.extract
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.nonemptylist.comonad.extract
import arrow.core.extensions.nonemptylist.functor.functor
import arrow.core.nel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.IMentioned
import kotlin.random.Random

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
        "Identified should not evaluate inner value" {
            var sideEffect = false

            val identified = snowflake identify {
                Eval.later {
                    sideEffect = true
                    entity
                }
            }

            sideEffect.shouldBeFalse()
            identified.id shouldBe snowflake
            identified().extract().id shouldBe snowflake
            sideEffect.shouldBeTrue()
        }

        "Identified should flatmap lazily" {
            var sideEffect = false

            val identified = snowflake identify {
                sideEffect = true
                entity.just()
            }

            sideEffect.shouldBeFalse()
            val new = identified.flatMap(Id.monad()) { snowflake identify { superEntity.just() } }
            sideEffect.shouldBeFalse()
            new().extract().mention shouldBe "Super cool mention"
            sideEffect.shouldBeTrue()
        }

        "Identified should map lazily" {
            var sideEffect = false

            val identified = snowflake identify {
                sideEffect = true
                entity.nel()
            }

            sideEffect.shouldBeFalse()
            val new = identified.map(Nel.functor()) { superEntity }
            sideEffect.shouldBeFalse()
            new().extract().mention shouldBe "Super cool mention"
            sideEffect.shouldBeTrue()
        }
    }
}