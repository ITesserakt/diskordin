package org.tesserakt.diskordin.commands

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class CommandBuilderTest : StringSpec({
    "Constructed command should contain all props from builder" {
        val command = CommandBuilder().apply {
            +name("Test")
            +description("Test command")
            +aliases("help", "wanted")
        }

        command.create() should {
            assertSoftly {
                it.name shouldBe "Test"
                it.aliases shouldContainAll listOf("help", "wanted")
                it.description shouldBe "Test command"
                it.isHidden shouldBe false
                it.requiredFeatures shouldHaveSize 0
            }
        }
    }
})