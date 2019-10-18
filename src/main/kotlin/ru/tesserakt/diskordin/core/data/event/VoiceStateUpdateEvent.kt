package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.json.response.VoiceStateResponse

class VoiceStateUpdateEvent(raw: VoiceStateResponse) : IEvent {
    val voiceState = raw.unwrap()
}
