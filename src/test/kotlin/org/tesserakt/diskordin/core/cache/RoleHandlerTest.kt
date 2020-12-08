package org.tesserakt.diskordin.core.cache

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.cache.CacheSnapshotBuilder.Companion.mutate
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.RoleResponse
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.impl.core.client.configure
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.Role
import org.tesserakt.diskordin.rest.WithoutRest

@DiscordClientBuilderScope.InternalTestAPI
class RoleHandlerTest : FunSpec({
    // many entities rely on discord client, for context, rest etc.
    runBlocking {
        DiscordClientBuilder by WithoutRest configure {
            +disableTokenVerification()
        }
    }

    val guild = Guild(
        GuildResponse(
            12345678911.asSnowflake(),
            "Test guild",
            null,
            null,
            owner_id = 12345678912.asSnowflake(),
            region = "",
            afk_channel_id = null,
            afk_timeout = -1,
            verification_level = 0,
            default_message_notifications = 0,
            explicit_content_filter = 0,
            roles = emptySet(),
            emojis = emptyList(),
            features = emptyList(),
            mfa_level = 0,
            application_id = null,
            system_channel_id = null,
            max_members = null,
            max_presences = null,
            vanity_url_code = null,
            description = null,
            banner = null,
            widget_channel_id = null,
            premiumSubscribersCount = null
        )
    )

    val fakeRole = Role(
        RoleResponse(
            12345678901.asSnowflake(),
            "Test",
            -1,
            false,
            -1,
            -1,
            managed = false,
            mentionable = false
        ), 12345678911.asSnowflake()
    )

    context("updater") {
        val handler = RoleUpdater

        test("Cache should be mutated after add") {
            var cache = MemoryCacheSnapshot.empty().copy(guilds = mapOf(guild.id to guild)).mutate()
            handler.handle(cache, fakeRole)
            val cachedGuild = cache.getGuild(guild.id) as? Guild

            cachedGuild.shouldNotBeNull()
            cachedGuild.raw shouldNotBeSameInstanceAs guild.raw
            cachedGuild.roles.map { (it as Role).raw } shouldContain fakeRole.raw
        }

        test("Cache should be mutated after modify") {
            val newGuild = guild.copy { it.copy(roles = setOf(fakeRole.raw)) }
            val newRole = fakeRole.copy { it.copy(color = 2) } as Role
            var cache = MemoryCacheSnapshot.empty().copy(guilds = mapOf(newGuild.id to newGuild)).mutate()
            val cachedGuild = cache.getGuild(newGuild.id)

            fakeRole.raw shouldNotBeSameInstanceAs newRole.raw
            cachedGuild.shouldNotBeNull()
            cachedGuild.roles.map { (it as Role).raw } shouldContain fakeRole.raw

            handler.handle(cache, newRole)

            cachedGuild.roles.map { (it as Role).raw } shouldNotContain newRole.raw
            cache.getGuild(newGuild.id)!!.roles.map { (it as Role).raw } shouldContain newRole.raw
        }

        test("Nothing should be changed if there is no guild in cache") {
            val cache = handler.handleAndGet(MemoryCacheSnapshot.empty().mutate(), fakeRole)

            cache.guilds.shouldBeEmpty()
        }
    }

    context("deleter") {
        val handler = RoleDeleter

        test("Nothing should happened with empty cache") {
            val cache = MemoryCacheSnapshot.empty().mutate()

            cache.guilds.shouldBeEmpty()
            handler.handle(cache, fakeRole)
            cache.guilds.shouldBeEmpty()
        }

        test("Item should be deleted from cache") {
            val cache = MemoryCacheSnapshot.empty().copy(guilds = mapOf(guild.id to guild)).mutate()
            RoleUpdater.handle(cache, fakeRole)
            handler.handle(cache, fakeRole)
            val cachedGuild = cache.getGuild(guild.id)

            cachedGuild.shouldNotBeNull()
            cachedGuild.roles.map { (it as Role).raw } shouldNotContain fakeRole
        }
    }
})
