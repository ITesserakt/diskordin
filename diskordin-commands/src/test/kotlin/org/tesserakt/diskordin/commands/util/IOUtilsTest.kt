package org.tesserakt.diskordin.commands.util

import arrow.fx.IO
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.extensions.observablek.applicativeError.attempt
import arrow.fx.rx2.extensions.observablek.async.async
import arrow.fx.rx2.extensions.singlek.applicativeError.attempt
import arrow.fx.rx2.extensions.singlek.async.async
import arrow.fx.rx2.fix
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeTypeOf

class IOUtilsTest : StringSpec() {
    init {
        "Normal IO should convert to F<>" {
            val io = IO {
                println("Simulating delay")
                1
            }

            val single = io.fromIO(SingleK.async()).fix()
            single.attempt().suspended() shouldBeRight {
                it shouldBe 1
            }
        }

        "IO with error should not convert to F<>" {
            val io = IO<Int> {
                error("Test")
            }

            val single = io.fromIO(ObservableK.async()).fix()
            @Suppress("BlockingMethodInNonBlockingContext")
            single.attempt().observable.blockingFirst() shouldBeLeft {
                it.shouldBeTypeOf<IllegalStateException>()
                it shouldHaveMessage "Test"
            }
        }
    }
}