package game.monopoly_game

import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.game_interfaces.DiceGame
import game.game_interfaces.TurnBasedGame
import game.monopoly_game.board.AbstractTile
import game.monopoly_game.board.MonopolyBoard
import game.monopoly_game.data.MonopolyGameplayParams
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.formatDiceRoll
import utils.instantiateStandardBoard

class MonopolyGame(
    val playerList: MutableMap<Int, MonopolyPlayer>,
    hostChannel: MessageChannel,
) : AbstractGame(
    playerList.values.map { p: MonopolyPlayer -> p.member }.toMutableSet(),
    hostChannel
), TurnBasedGame, DiceGame {
    val board: MonopolyBoard = instantiateStandardBoard()
    private val params: MonopolyGameplayParams = MonopolyGameplayParams()

    private val numBankrupt: Int
        get() = playerList.values.count { p: MonopolyPlayer -> p.isBankrupt }

    override var turn: Int = 0
    override var ply: Int = 0
    override val memberTurns: MutableMap<Int, Member> = playerList
        .mapValues { pair: Map.Entry<Int, MonopolyPlayer> ->
            pair.value.member
        }.toMutableMap()

    override val turnPlayer: MonopolyPlayer
        get() = playerList[ply]
            ?: throw IllegalStateException("Current ply (#$ply) doesn't correspond to any player")

    override fun incrementPly(): Unit = runBlocking {
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

        super.incrementPly()
        diceRoll.clear()

        if (turnPlayer.isBankrupt) incrementPly()
    }

    override val diceRoll: ArrayList<Int> = arrayListOf()

    /**
     * @return Whether the turn can end (i.e. all issues resolved)
     */
    fun canEndTurn(): Boolean = runBlocking {
        if (diceRoll.isEmpty()) {
            launch { sendMessage("Roll dice before ending turn") }
            return@runBlocking false
        }

        if (diceRoll.toSet().size == 1) {
            launch { sendMessage("Previously rolled a double: ${formatDiceRoll(diceRoll)}") }
        }

        if (turnPlayer.money < 0) {
            if (turnPlayer.owns.isNotEmpty()) {
                launch { sendMessage(notifyNegBalance(turnPlayer)) }
                return@runBlocking false
            }

            // Else, the player has nothing left, and must is forced to declare bankruptcy
            declareBankruptcy(turnPlayer)

            return@runBlocking true
        }

        return@runBlocking true
    }

    fun declareBankruptcy(bankruptPlayer: MonopolyPlayer): Unit {
        val embezzledFunds: Int = bankruptPlayer.money / (playerList.size - numBankrupt)
        playerList.forEach { playerEntry: Map.Entry<Int, MonopolyPlayer> ->
            if (!playerEntry.value.isBankrupt) playerEntry.value.addMoney(embezzledFunds)
        }
        bankruptPlayer.deductMoney(bankruptPlayer.money)
        bankruptPlayer.isBankrupt = true
    }

    fun roll(): ArrayList<Int> = super.roll(params.numberOfDice, params.numberOfFaces)

    /**
     * Returns the tile that this player is currently standing on
     */
    fun MonopolyPlayer.getTile(): AbstractTile = board.tileset[this.position]

    companion object {
        fun notifyNegBalance(player: MonopolyPlayer): String =
            "${player.name}'s balance is negative at \$${player.money}, " +
            "resolve debts before ending turn or declare bankruptcy"
    }
}