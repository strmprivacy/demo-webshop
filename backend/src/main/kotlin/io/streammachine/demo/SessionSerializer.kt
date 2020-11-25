package io.streammachine.demo

import io.ktor.sessions.*

data class Session(val sessionId: String)

class UltimateStoreSessionSerializer : SessionSerializer<Session> {
    override fun deserialize(text: String): Session = Session(text)
    override fun serialize(session: Session): String = session.sessionId
}

