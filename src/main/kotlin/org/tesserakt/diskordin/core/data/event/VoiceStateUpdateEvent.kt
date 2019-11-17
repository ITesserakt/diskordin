package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.json.response.VoiceStateResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class VoiceStateUpdateEvent(raw: VoiceStateResponse) : IEvent {
    val voiceState = raw.unwrap()
}
