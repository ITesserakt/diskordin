package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.json.response.VoiceStateResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap

class VoiceStateUpdateEvent(raw: VoiceStateResponse) : IEvent {
    val voiceState = raw.unwrap()
}
