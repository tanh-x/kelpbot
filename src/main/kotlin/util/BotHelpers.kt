package util

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

suspend fun Message.respond(content: String) {
    this.channel.createMessage(content)
}

suspend fun Message.respond(builder: UserMessageCreateBuilder.() -> Unit) {
    this.channel.createMessage(builder)
}

fun Message.canInvokeCommand(cat: CommandCategory): Boolean = cat.isInvocableByMessage(this)

fun Message.canInvokeCommand(cmd: BotCommand): Boolean = cmd.getCategory().isInvocableByMessage(this)

fun Message.canInvokeCommand(): Boolean = this.getCommand(true) != null

fun Message.getCommand(checkInvocable: Boolean = false): BotCommand? {
    val invocation: String = getArgs(this.content)[0].substring(1)

    return CommandCategory.values()
        .map { cat: CommandCategory ->
            if (checkInvocable) if (cat.isInvocableByMessage(this)) cat.commands else emptyArray()
            else cat.commands
        }
        .reduce { acc: Array<BotCommand>, c: Array<BotCommand> -> acc + c }
        .firstOrNull { cmd: BotCommand ->
            return@firstOrNull invocation in cmd.invocations
        }
}

fun getArgs(s: String): Array<String> = s
    .split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
    .filter { x -> x.isNotBlank() }
    .toTypedArray()

