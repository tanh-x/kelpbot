package bot.command

import dev.kord.core.entity.Message

data class BotCommand(val invocation: Array<String>, val execute: suspend (Message, Array<String>) -> Unit) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BotCommand
        if (!invocation.contentEquals(other.invocation)) return false
        if (execute != other.execute) return false
        return true
    }

    override fun hashCode(): Int = 31 * invocation.contentHashCode() + execute.hashCode()
}