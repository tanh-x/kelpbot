package bot.command

import dev.kord.core.entity.Message

/**
 * Data class representing a command.
 *
 * @param invocations possible words to call this command's [execute] method
 * @param descriptor description for this command to print in the help string
 * @param execute the function that will be called when the command is invoked
 */
data class BotCommand(
    val invocations: Array<String>,
    val descriptor: String = invocations[0],
    val execute: suspend (Message, Array<String>) -> Unit = { _, _ -> },
    val isInvocable: Message.() -> Boolean = { true }
) {
    val category: CommandCategory
        get() = CommandCategory.values().firstOrNull { cat: CommandCategory -> this in cat.commands }
            ?: throw NoSuchElementException("Command was not found in within any defined categories")
    // A command always lives in one category

    init {
        if (invocations.isEmpty()) {
            throw IllegalArgumentException("Command with no invocations instantiated in ${category}")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BotCommand
        if (!invocations.contentEquals(other.invocations)) return false
        if (execute != other.execute) return false
        return true
    }

    override fun hashCode(): Int = 31 * invocations.contentHashCode() + execute.hashCode()
}