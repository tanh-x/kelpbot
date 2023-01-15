package util

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

suspend fun Message.respond(content: String) {
    this.channel.createMessage(content)
}

suspend fun Message.respond(builder: UserMessageCreateBuilder.() -> Unit) {
    this.channel.createMessage(builder)
}

fun splitArguments(s: String): Array<String> {
    return s.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
        .filter { x -> x.isNotBlank() }
        .toTypedArray()
}