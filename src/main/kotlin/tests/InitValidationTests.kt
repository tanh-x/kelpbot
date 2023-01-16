package tests

import bot.command.BotCommand
import bot.command.CommandCategory

object InitValidationTests {
    @JvmStatic
    fun testCommandDuplication(cats: Array<CommandCategory>) {
        val duplicateInvocations: Map<String, Int> = cats
            .map { cat: CommandCategory -> cat.commands }
            .reduce { acc: Array<BotCommand>, c: Array<BotCommand> -> acc + c }
            .map { cmd: BotCommand -> cmd.invocations }
            .reduce { acc: Array<String>, s: Array<String> -> acc + s }
            .groupingBy { it }.eachCount()
            .filter { pair: Map.Entry<String, Int> -> pair.value > 1 }
        if (duplicateInvocations.isNotEmpty()) {
            throw IllegalStateException("Found duplicate command invocations: $duplicateInvocations")
        }
    }
}