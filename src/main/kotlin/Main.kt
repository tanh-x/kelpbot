import bot.Bot
import java.io.File

suspend fun main() {
    Bot(File("./token.txt").readLines().first()).main()
}