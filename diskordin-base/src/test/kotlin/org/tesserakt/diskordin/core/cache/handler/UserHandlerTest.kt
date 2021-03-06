package org.tesserakt.diskordin.core.cache.handler

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.cache.CacheSnapshotBuilder.Companion.mutate
import org.tesserakt.diskordin.core.cache.MemoryCacheSnapshot
import org.tesserakt.diskordin.core.client.InternalTestAPI
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.core.data.json.response.IDUserResponse
import org.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.entity.MessageUser
import org.tesserakt.diskordin.impl.core.entity.Self
import org.tesserakt.diskordin.rest.WithoutRest

@InternalTestAPI
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
        1234,
        "*hash*",
        bot = true,
        mfa_enabled = false,
        locale = "ru-RU",
        publicFlags = 0,
        premium_type = 0
    ).unwrap()

    val fakeIdUser = IDUserResponse(
        id,
        "Test2",
        1235,
        null,
        bot = false,
        mfaEnabled = true,
        locale = "Aether",
        verified = false,
        email = "Overworld",
        publicFlags = 1,
        premiumType = 1,
        system = null
    ).unwrap()

    val fakeMessageUser = MessageUserResponse(
        "Test3",
        id,
        1236,
        "not a hash"
    ).unwrap()

    context("updater") {
        val handler = UserUpdater

        test("Item should appear after adding") {
            val cache = handler.handleAndGet(MemoryCacheSnapshot.empty().mutate(), fakeUser)
            cache.getUser(id).shouldNotBeNull()
        }

        test("Fields of user should update after adding idUser") {
            var cache = MemoryCacheSnapshot.empty().mutate()
            cache = handler.handleAndGet(cache, fakeUser)
            cache = handler.handleAndGet(cache, fakeIdUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
            cachedUser.raw.asClue {
                it.username shouldBe "Test2"
                it.discriminator shouldBe 1235
                it.avatar shouldBe "*hash*"
                it.bot shouldBe false
                it.mfa_enabled shouldBe true
                it.locale shouldBe "Aether"
                it.publicFlags shouldBe 1
                it.premium_type shouldBe 1
            }
        }

        test("Fields of user should update after adding messageUser") {
            var cache = MemoryCacheSnapshot.empty().mutate()
            cache = handler.handleAndGet(cache, fakeUser)
            cache = handler.handleAndGet(cache, fakeMessageUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<MessageUser>()
            cachedUser.raw shouldBe (fakeMessageUser as MessageUser).raw
        }

        test("Fields of idUser should update after adding user") {
            var cache = MemoryCacheSnapshot.empty().mutate()
            cache = handler.handleAndGet(cache, fakeIdUser)
            cache = handler.handleAndGet(cache, fakeUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
            cachedUser.raw shouldBe (fakeUser as Self).raw
        }

        test("Fields of idUser should update after adding messageUser") {
            var cache = MemoryCacheSnapshot.empty().mutate()
            cache = handler.handleAndGet(cache, fakeIdUser)
            cache = handler.handleAndGet(cache, fakeMessageUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<MessageUser>()
            cachedUser.raw.asClue {
                it.username shouldBe "Test3"
                it.discriminator shouldBe 1236
                it.avatar shouldBe "not a hash"
                it.bot shouldBe false
            }
        }

        test("Fields of messageUser should update after adding user") {
            var cache = MemoryCacheSnapshot.empty().mutate()
            cache = handler.handleAndGet(cache, fakeMessageUser)
            cache = handler.handleAndGet(cache, fakeUser)
            val cachedUser = cache.getUser(id)

            cachedUser.shouldBeTypeOf<Self>()
            cachedUser.raw shouldBe (fakeUser as Self).raw
        }

        test("Fields of messageUser should update after adding idUser") {
            var cache = MemoryCacheSnapshot.empty().mutate()
            cache = handler.handleAndGet(cache, fakeMessageUser)
            cache = handler.handleAndGet(cache, fakeIdUser)
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
            val cache = MemoryCacheSnapshot.empty().copy(users = mapOf(id to fakeIdUser)).mutate()
            handler.handle(cache, fakeIdUser)

            cache.getUser(id).shouldBeNull()
        }
    }
})
