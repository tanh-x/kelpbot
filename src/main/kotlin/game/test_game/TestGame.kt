package game.test_game

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.AbstractPlayer
import game.game_interfaces.TurnBasedGame
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestGame(
    playerList: MutableSet<TestPlayer>,
    hostChannel: MessageChannel,
) : AbstractGame(
    playerList.map { p: TestPlayer -> p.member }.toMutableSet(),
    hostChannel
), TurnBasedGame {
    override var turn: Int = 0
    override var ply: Int = 0

    override val memberTurns: MutableMap<Int, Member> = mutableMapOf()
    override val turnPlayer: TestPlayer = playerList.first()



    override fun incrementPly(): Unit = runBlocking {
        super.incrementPly()
        launch { sendMessage("End turn") }
    }

    override fun startGame(): Unit = runBlocking {
        launch { sendMessage("Starting the test game") }
    }

    override fun User.fetchPlayer(): AbstractPlayer = turnPlayer
}