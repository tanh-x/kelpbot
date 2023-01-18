package game

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import io.ktor.util.date.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class AbstractGame(
    /**
     * Stores the list of players participating in the game. Uses a [MutableSet] of [Member]s
     * as we want to access server-specific information.
     */
    private var userList: MutableSet<Member>,
    hostChannel: MessageChannel,
) {

    /**
     * Whether the game is still in progress
     */
    var isOngoing: Boolean = false
        private set

    /**
     * Whether new players can still join
     */
    var isJoinable: Boolean = true
        protected set

    /**
     * Timestamp of the game's creation (recorded when the class is initialised, not when
     * the game is started). Used to disband abandoned games unless forcibly kept alive
     */
    val createdOnMilis: Long = getTimeMillis()

    val channel: MessageChannel = hostChannel

    /**
     * Attempts to start the game, called by the user with the start command
     */
    open fun startGame(): Unit = runBlocking {
        isOngoing = true
        launch { sendMessage("Starting game with ${userList.size} players") }
    }

    /**
     * Attempts to add the specified user to the game
     */
    open fun addPlayer(member: Member): Boolean = runBlocking {
        if (!isJoinable) {
            launch { sendMessage("This game is no longer open to new players") }
            return@runBlocking false
        }

        if (!userList.add(member)) {
            launch { sendMessage("${member.displayName} is already in this game") }
            return@runBlocking false
        }

        launch { sendMessage("Added ${member.displayName} to this game") }
        return@runBlocking true
    }

    open fun getFormattedPlayerList(): String {
        return userList.joinToString(", ") { p: Member -> p.displayName }
    }

    abstract fun User.fetchPlayer(): AbstractPlayer?

    /**
     * More succinct method call to send a message to the channel that the game is hosted on
     */
    protected suspend fun sendMessage(content: String): Unit {
        channel.createMessage(content)
    }

    protected suspend fun sendMessage(builder: UserMessageCreateBuilder.() -> Unit): Unit {
        channel.createMessage(builder)
    }
}