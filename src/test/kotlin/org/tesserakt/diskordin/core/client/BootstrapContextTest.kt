package org.tesserakt.diskordin.core.client

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

private object TestExtensionContext : BootstrapContext.ExtensionContext
private object TestExtension : BootstrapContext.Extension<TestExtensionContext>
private object TestPersistentExtension : BootstrapContext.PersistentExtension<TestExtensionContext>
private object AnotherPersistentExtension : BootstrapContext.PersistentExtension<TestExtensionContext>

class BootstrapContextTest : FunSpec() {
    init {
        test("actions with extension") {
            val container = BootstrapContext(emptyMap())

            container[TestExtension].shouldBeNull()
            container[TestExtension] = TestExtensionContext
            container[TestExtension].shouldNotBeNull()
        }

        test("actions with persistent extension") {
            val container = BootstrapContext(mapOf(TestPersistentExtension to TestExtensionContext))

            shouldNotThrow<Throwable> { container[TestPersistentExtension] }

            shouldThrow<NullPointerException> { container[AnotherPersistentExtension] }
            container[AnotherPersistentExtension] = TestExtensionContext
            shouldNotThrow<Throwable> { container[AnotherPersistentExtension] }
        }
    }
}