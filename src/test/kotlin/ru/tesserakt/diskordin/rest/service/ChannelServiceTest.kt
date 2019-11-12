package ru.tesserakt.diskordin.rest.service

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.fix
import arrow.fx.typeclasses.Bracket
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBeTrue
import org.junit.Test
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import ru.tesserakt.diskordin.callAndExtract
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.MessagesQuery
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.getRight
import ru.tesserakt.diskordin.rest.RestClient
import ru.tesserakt.diskordin.withFinalize
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@TestMethodOrder(MethodOrderer.Random::class)
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class ChannelServiceTest : Bracket<ForIO, Throwable> by IO.bracket() {
    private val constPinMessage = 641979565593460747.asSnowflake()
    private val constChannelId = 641701349914050560.asSnowflake()
    private val constMessageId = 641979535235088405.asSnowflake()
    private val constEditMessageId = 642024602142244874.asSnowflake()
    private val constReactionsMessageId = 642070657303314442.asSnowflake()
    private val rest = RestClient(defaultRetrofit, IO.async())

    @Test
    fun `retrieve existing channel from discord`() {
        val channel = rest.callAndExtract { channelService.getChannel(constChannelId) }
            .unsafeRunSync()

        channel.isRight().shouldBeTrue()
        channel.getRight().type `should equal` IChannel.Type.GuildText.ordinal
        channel.getRight().id `should equal` constChannelId
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun `change channel name from editChannel`() = withFinalize(rest.callAndExtract {
        channelService.editChannel(constChannelId, TextChannelEditBuilder().build {
            name = "testnew"
        }, "Test")
    }, {
        it.isRight().shouldBeTrue()
        it.getRight().name `should equal` "testnew"
    }, {
        rest.callRaw {
            channelService.editChannel(constChannelId, TextChannelEditBuilder().build {
                name = "testing"
            }, "Recover")
        }
    }).fix().unsafeRunSync()

    @Test
    fun `trigger typing indicator in text channel`() = rest.effect {
        channelService.triggerTyping(constChannelId)
    }.fix().unsafeRunSync()

    @Test
    fun `get 100 last messages from text channel`() {
        val messages = rest.callRaw {
            channelService.getMessages(constChannelId, MessagesQuery().apply {
                this.limit = 100
            }.create())
        }.attempt().fix().unsafeRunSync()

        messages.isRight().shouldBeTrue()
        messages.getRight().map { it.id } `should contain` constMessageId
        messages.getRight().map { it.content } `should contain` "And it"
    }

    @Test
    fun `retrieve existing message from discord`() {
        val message = rest.callAndExtract {
            channelService.getMessage(constChannelId, constMessageId)
        }.unsafeRunSync()

        message.getRight().id `should equal` constMessageId
        message.getRight().content `should equal` "It`s a test message!"
    }

    @Test
    @Suppress("RemoveExplicitTypeArguments")
    fun `create message and then delete it`() = withFinalize(rest.callAndExtract {
        channelService.createMessage(constChannelId, MessageCreateBuilder().build {
            content = "Test"
            tts = true
            nonce = null
            embed = {
                url = "https://example.com"
                fields = arrayOf<EmbedCreateBuilder.FieldBuilder.() -> Unit>(
                    {
                        value = "huh"
                        name = "Huh"
                        inline = null
                    }
                )
            }
        })
    }, {
        it.isRight().shouldBeTrue()
        it.getRight().content `should equal` "Test"
        it.getRight().embeds.size `should equal` 1
        it.getRight().channel_id `should equal` constChannelId
    }, {
        it.isRight().shouldBeTrue()
        rest.effect {
            channelService.deleteMessage(constChannelId, it.getRight().id, "Recover")
        }
    }).fix().unsafeRunSync()

    @Test
    fun `change message content`() = withFinalize(rest.callRaw {
        channelService.editMessage(constChannelId, constEditMessageId, MessageEditBuilder().build {
            content = "It`s an edited test message!"
        })
    }.map { it.extract() }.attempt(), {
        it.isRight().shouldBeTrue()
        it.getRight().content `should equal` "It`s an edited test message!"
    }, {
        rest.callRaw {
            channelService.editMessage(constChannelId, constEditMessageId, MessageEditBuilder().build {
                content = "Message to edit"
            })
        }
    }).fix().unsafeRunSync()

    @Test
    fun `get all pinned messages`() {
        val messages = rest.callRaw { channelService.getPinnedMessages(constChannelId) }
            .fix().attempt().unsafeRunSync()

        messages.isRight().shouldBeTrue()

        messages.getRight().size `should equal` 1
        messages.getRight()[0].id `should equal` constMessageId
    }

    @Test
    fun `pin message and then unpin`() = withFinalize(rest.effect {
        channelService.pinMessage(constChannelId, constPinMessage)
    }.attempt(), {
        it.isRight().shouldBeTrue()
    }, {
        rest.effect {
            channelService.unpinMessage(constChannelId, constPinMessage)
        }
    }).fix().unsafeRunSync()

    @Test
    fun `add ok reaction and then remove it`() = withFinalize(rest.effect {
        channelService.addReaction(constChannelId, constMessageId, "\uD83D\uDC4D")
    }.attempt(), {
        it.isRight().shouldBeTrue()
    }, {
        rest.effect {
            channelService.removeOwnReaction(constChannelId, constMessageId, "\uD83D\uDC4D")
        }
    }).fix().unsafeRunSync()

    @Test
    fun `get all reactions on message`() {
        val messages = rest.callRaw {
            channelService.getReactions(
                constChannelId,
                constReactionsMessageId,
                "\uD83D\uDC4D",
                ReactedUsersQuery().create()
            )
        }.fix().attempt().unsafeRunSync()

        messages.isRight().shouldBeTrue()

        messages.getRight().size `should equal` 1
        messages.getRight()[0].id `should equal` 316249690092077065.asSnowflake()
    }

    @Test
    fun `retrieve channel invites from discord`() {
        val invites = rest.callRaw {
            channelService.getChannelInvites(constChannelId)
        }.attempt().fix().unsafeRunSync()

        invites.isRight().shouldBeTrue()

        invites.getRight().map { it.code } `should contain` "efa9KMy"
    }

    @Test
    @ExperimentalTime
    fun `create an invite and check it channel id`() {
        val invite = rest.callAndExtract {
            channelService.createChannelInvite(constChannelId, InviteCreateBuilder().build {
                isTemporary = true
                maxAge = 15.seconds
            }, "Test")
        }.unsafeRunSync()

        invite.isRight().shouldBeTrue()
        invite.getRight().channel.id `should equal` constChannelId
    }
}