package game.monopoly_game

import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.game_interfaces.DiceGame
import game.game_interfaces.TurnBasedGame
import game.monopoly_game.board.MonopolyBoard
import game.monopoly_game.data.MonopolyGameplayParams
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.instantiateStandardBoard

class MonopolyGame(
    val playerList: MutableSet<MonopolyPlayer>,
    hostChannel: MessageChannel,
) : AbstractGame(
    playerList.map { p: MonopolyPlayer -> p.member }.toMutableSet(),
    hostChannel
), TurnBasedGame, DiceGame {
    val board: MonopolyBoard = instantiateStandardBoard()
    private val params: MonopolyGameplayParams = MonopolyGameplayParams()

    override var turn: Int = 0
    override var ply: Int = 0

    override fun incrementTurn(): Unit = runBlocking {
        val failedChecks: Set<ValidationChecks> = MonopolyGameStateValidator.validateGame(
            game = this@MonopolyGame, onlyReturnFails = true
        ).keys

        if (failedChecks.isNotEmpty()) launch {
            sendMessage(
                "${failedChecks.size} validation checks failed at the end of turn: \n" +
                failedChecks.map { check: ValidationChecks ->
                    " - ${check.name}: ${check.description}"
                }.toString()
            )
        }

        super.incrementTurn()
    }

    override val playerTurns: MutableMap<Int, Member> = playerList
        .mapIndexed { i: Int, p: MonopolyPlayer -> Pair(i, p.member) }
        .toMap(mutableMapOf())

    override val diceRoll: ArrayList<Int> = arrayListOf()

    fun roll(): ArrayList<Int> = super.roll(params.numberOfDice, params.numberOfFaces)
}