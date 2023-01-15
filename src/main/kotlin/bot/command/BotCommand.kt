package bot.command

import dev.kord.core.entity.Message

data class BotCommand(val invocation: Array<String>, val execute: suspend (Message) -> Unit) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BotCommand
        if (!invocation.contentEquals(other.invocation)) return false
        if (execute != other.execute) return false
        return true
    }

    override fun hashCode(): Int {
        var result = invocation.contentHashCode()
        result = 31 * result + execute.hashCode()
        return result
    }
}