package org.tesserakt.diskordin.rest.service

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.fix
import arrow.fx.typeclasses.Bracket
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.withFinalize

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class TearDownTest : Bracket<ForIO, Throwable> by IO.bracket() {
    private var test = 0

    @Test
    fun `use a normal io and finalize it`() = withFinalize(IO.effect {
        test++
        test
    }, {
        it `should be equal to` 1
    }, {
        (test--).just()
    }).fix().unsafeRunSync().also {
        test `should be equal to` 0
    }

    @Test
    fun `must not run finalizer and use if io crashed`() {
        {
            withFinalize(IO {
                error("test")
            }, {
                false.shouldBeTrue()
            }, {
                false.shouldBeTrue().just()
            }).fix().unsafeRunSync()
        } shouldThrow IllegalStateException::class `with message` "test"
    }

    @Test
    fun `must run finalizer only if use failed and init not`() {
        {
            withFinalize(IO { 1 }, {
                it `should equal` -1
            }, {
                false.shouldBeTrue().just()
            }).fix().unsafeRunSync()
        } `should throw` AssertionError::class `with message` "Expected value to be true, but was false"
    }
}