package util

import dev.kord.core.entity.Message

suspend fun Message.respond(content: String) {
    this.channel.createMessage(content)
}