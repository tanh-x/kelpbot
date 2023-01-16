package utils

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

suspend fun Message.respond(s: String): Unit {
    this.channel.createMessage(s)
}

suspend fun Message.respond(builder: UserMessageCreateBuilder.() -> Unit): Unit {
    this.channel.createMessage(builder)
}

suspend fun Message.reply(s: String): Unit {
    this.reply { this.content = s }
}

fun Message.canInvokeCommand(cat: CommandCategory): Boolean = cat.isInvocableByMessage(this)

fun Message.canInvokeCommand(cmd: BotCommand): Boolean =
    cmd.getCategory().isInvocableByMessage(this) && cmd.isInvocable(this)

fun Message.canInvokeCommand(): Boolean = this.getCommand(true) != null

fun Message.getCommand(checkInvocable: Boolean = false): BotCommand? {
    val invocation: String = getArgs(this.content)[0].substring(1).lowercase()

    return CommandCategory.values()
        .filter { cat: CommandCategory -> !checkInvocable || cat.isInvocableByMessage(this) }
        .map { cat: CommandCategory -> cat.commands }
        .reduce { acc: Array<BotCommand>, c: Array<BotCommand> -> acc + c }
        .firstOrNull { cmd: BotCommand ->
            return@firstOrNull invocation in cmd.invocations && (!checkInvocable || cmd.isInvocable(this))
        }
}

fun getArgs(s: String): Array<String> = s
    .split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
    .filter { x -> x.isNotBlank() }
    .toTypedArray()

