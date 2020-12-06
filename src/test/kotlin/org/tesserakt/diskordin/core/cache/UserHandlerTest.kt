package org.tesserakt.diskordin.core.cache

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.core.data.json.response.IDUserResponse
import org.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.impl.core.entity.IdUser
import org.tesserakt.diskordin.impl.core.entity.MessageUser
import org.tesserakt.diskordin.impl.core.entity.Self
import org.tesserakt.diskordin.rest.WithoutRest

@DiscordClientBuilderScope.InternalTestAPI
class UserHandlerTest : FunSpec({
    runBlocking {
        DiscordClientBuilder[WithoutRest] {
            +disableTokenVerification()
        }
    }

    val id = 12345678910.asSnowflake()

    val fakeUser = UserResponse<IUser>(
        id,
        "Test",
        "1234",
        "*hash*",
        bot = true,
        mfa_enabled = false,
        locale = "ru-RU",
        verified = true,
        email = "Email",
        flags = null,
        premium_type = 0
    ).unwrap()

    val fakeIdUser = IDUserResponse(
        id,
        "Test2",
        "1235",
        null,
        bot = false,
        mfaEnabled = true,
        locale = "Aether",
        verified = false,
        email = "Overworld",
        flags = 1,
        premiumType = 1
    ).unwrap()

    val fakeMessageUser = MessageUserResponse(
        "Test3",
        id,
        1236,
        "not a hash",
        null,
        null
    ).unwrap()

    context("updater") {
        val handler = UserUpdater

        test("Item should appear after adding") {
            val cache = handler.handle(MemoryCacheSnapshot.empty(), fakeUser)
            cache.getUser(id).shouldNotBeNull()
        }

        test("Fields of user should update after adding idUser") {
            var cache = MemoryCacheSnapshot.empty()
            cache = handler.handle(cache, fakeUser)
            cache = handler.handle(cache, fakeIdUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
            cachedUser.raw.asClue {
                it.username shouldBe "Test2"
                it.discriminator shouldBe "1235"
                it.avatar shouldBe "*hash*"
                it.bot shouldBe false
                it.mfa_enabled shouldBe true
                it.locale shouldBe "Aether"
                it.verified shouldBe false
                it.email shouldBe "Overworld"
                it.flags shouldBe 1
                it.premium_type shouldBe 1
            }
        }

        test("Fields of user should update after adding messageUser") {
            var cache = MemoryCacheSnapshot.empty()
            cache = handler.handle(cache, fakeUser)
            cache = handler.handle(cache, fakeMessageUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
            cachedUser.raw.asClue {
                it.username shouldBe "Test3"
                it.discriminator shouldBe "1236"
                it.avatar shouldBe "not a hash"
                it.bot shouldBe true
                it.mfa_enabled shouldBe false
                it.locale shouldBe "ru-RU"
                it.verified shouldBe true
                it.email shouldBe "Email"
                it.flags shouldBe null
                it.premium_type shouldBe 0
            }
        }

        test("Fields of idUser should update after adding user") {
            var cache = MemoryCacheSnapshot.empty()
            cache = handler.handle(cache, fakeIdUser)
            cache = handler.handle(cache, fakeUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
            // all fields are replaced and there is no need to check
        }

        test("Fields of idUser should update after adding messageUser") {
            var cache = MemoryCacheSnapshot.empty()
            cache = handler.handle(cache, fakeIdUser)
            cache = handler.handle(cache, fakeMessageUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<IdUser>()
            cachedUser.raw.asClue {
                it.username shouldBe "Test3"
                it.discriminator shouldBe "1236"
                it.avatar shouldBe "not a hash"
                it.bot shouldBe false
            }
        }

        test("Fields of messageUser should update after adding user") {
            var cache = MemoryCacheSnapshot.empty()
            cache = handler.handle(cache, fakeMessageUser)
            cache = handler.handle(cache, fakeUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
        }

        test("Fields of messageUser should update after adding idUser") {
            var cache = MemoryCacheSnapshot.empty()
            cache = handler.handle(cache, fakeMessageUser)
            cache = handler.handle(cache, fakeIdUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<MessageUser>()
            cachedUser.raw.asClue {
                it.username shouldBe "Test2"
                it.discriminator shouldBe 1235
                it.avatar shouldBe "not a hash"
                it.bot shouldBe false
            }
        }
    }

    context("deleter") {
        val handler = UserDeleter

        test("Item should be deleted from cache") {
            var cache = MemoryCacheSnapshot.empty().copy(users = mapOf(id to fakeIdUser))
            cache = handler.handle(cache, fakeIdUser)

            cache.getUser(id).shouldBeNull()
        }
    }
})
