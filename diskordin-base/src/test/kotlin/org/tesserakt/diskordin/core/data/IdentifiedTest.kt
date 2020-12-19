package org.tesserakt.diskordin.core.data

import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.ForNonEmptyList
import arrow.core.extensions.eval.comonad.extract
import arrow.core.extensions.id.applicative.just
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
        "org.tesserakt.diskordin.core.data.Identified should not evaluate inner value" {
            var sideEffect = false

            val identified = snowflake.identify<ForEval, IEntity> {
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

        "org.tesserakt.diskordin.core.data.Identified should map lazily" {
            var sideEffect = false

            val identified = snowflake.identify<ForNonEmptyList, IEntity> {
                sideEffect = true
                entity.nel()
            }

            sideEffect.shouldBeFalse()
            val new: Identified<IMentioned> = identified.map { superEntity.just() }
            sideEffect.shouldBeFalse()
            new().mention shouldBe "Super cool mention"
            sideEffect.shouldBeTrue()
        }
    }
}