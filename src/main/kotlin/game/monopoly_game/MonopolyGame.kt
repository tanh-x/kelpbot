package game.monopoly_game

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
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
    private var rollsLeft: Int = params.maxDoubleRolls

    override val turnPlayer: MonopolyPlayer
        get() = playerList[ply]
            ?: throw IllegalStateException("Current ply (#$ply) doesn't correspond to any player")

    override fun incrementPly(): Unit = runBlocking {
        // Validation checks
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

        // Sets things up for the next ply
        if (turnPlayer.position != board.jailIdx) turnPlayer.jailTurns = -1
        super.incrementPly()
        diceRoll.clear()
        rollsLeft = params.maxDoubleRolls

        // Skip bankrupt players
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

    fun roll(): ArrayList<Int> {
        if ((diceRoll.isNotEmpty() && !wasDoubleRoll())) return diceRoll
        super.roll(params.numberOfDice, params.numberOfFaces)

        if (turnPlayer.jailTurns > 0) {
            if (turnPlayer.position != board.jailIdx) return diceRoll
            if (wasDoubleRoll()) {  // Get out of jail by double roll
                turnPlayer.moveBy(diceSum)
            }
            return diceRoll
        }

        rollsLeft -= 1
        if (rollsLeft == 0 && wasDoubleRoll()) sendToJail(turnPlayer)

        turnPlayer.moveBy(diceSum)
        return diceRoll
    }

    /**
     * @param newPosition Tile index to move the player to, can be over the size of the board,
     * in which case the game will reward based on how many times the player would've stepped
     * past the Go tile, if [collectGoReward] is true
     * @param collectGoReward Whether to reward the player with the Go reward if applicable
     */
    private fun MonopolyPlayer.moveTo(newPosition: Int, collectGoReward: Boolean = false): Unit {
        if (collectGoReward) this.addMoney(newPosition / board.size)
        this.position = newPosition % board.size
        board.tileset[newPosition].onPlayerStep(this, this@MonopolyGame)
    }

    private fun MonopolyPlayer.moveBy(steps: Int): Unit {
        this.moveTo(this.position + steps, true)
    }

    private fun sendToJail(player: MonopolyPlayer): Unit {
        player.moveTo(board.jailIdx)
        player.jailTurns = params.jailDiceRolls
    }

    /**
     * Returns the tile that this player is currently standing on
     */
    fun MonopolyPlayer.getTile(): AbstractTile = board.tileset[this.position]

    override fun User.fetchPlayer(): MonopolyPlayer? {
        return playerList.values.firstOrNull { player: MonopolyPlayer -> this.id.value == player.uid }
    }

    companion object {
        fun notifyNegBalance(player: MonopolyPlayer): String =
            "${player.name}'s balance is negative at \$${player.money}, " +
            "resolve debts before ending turn or declare bankruptcy"
    }
}