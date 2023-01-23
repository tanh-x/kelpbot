package game.monopoly_game

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.game_interfaces.DiceGame
import game.game_interfaces.TurnBasedGame
import game.monopoly_game.board.AbstractTile
import game.monopoly_game.board.MonopolyBoard
import game.monopoly_game.board.Purchasable
import game.monopoly_game.data.MonopolyConstants
import game.monopoly_game.data.MonopolyGameplayParams
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.BotConstants
import utils.BotConstants.COIN
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

        if (!canEndTurn()) return@runBlocking

        // Sets things up for the next ply
        if (turnPlayer.position != board.jailIdx) turnPlayer.jailTurns = -1
        super.incrementPly()
        diceRoll.clear()
        rollsLeft = params.maxDoubleRolls

        // Skip bankrupt players
        if (turnPlayer.isBankrupt) incrementPly()
    }

    override val diceRoll: ArrayList<Int> = arrayListOf()

    override fun addMember(
        member: Member,
        allowMultiControl: Boolean,
        isHost: Boolean
    ): Boolean {
        if (!super.addMember(member, allowMultiControl, isHost)) return false

        playerList[playerList.size] = MonopolyPlayer(member, isHost, params)
        memberTurns[memberTurns.size] = member
        return true
    }

    /**
     * @return Whether the turn can end (i.e. all issues resolved)
     */
    private fun canEndTurn(): Boolean = runBlocking {
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
        if (playerList.size == 1) return  // TODO: Win condition

        val embezzledFunds: Int = bankruptPlayer.money / (playerList.size - numBankrupt)
        playerList.forEach { playerEntry: Map.Entry<Int, MonopolyPlayer> ->
            if (!playerEntry.value.isBankrupt) playerEntry.value.addMoney(embezzledFunds)
        }
        bankruptPlayer.deductMoney(bankruptPlayer.money)
        bankruptPlayer.isBankrupt = true
    }

    override fun roll(numDice: Int, numFace: Int): ArrayList<Int> {
        if ((diceRoll.isNotEmpty() && !wasDoubleRoll())) {
            return diceRoll
        }
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
        if (collectGoReward && newPosition >= board.size) {
            this.addMoney(newPosition / board.size * params.passGoReward)
        }
        this.position = newPosition % board.size
        board.tileset[this.position].onPlayerStep(this, this@MonopolyGame)
    }

    private fun MonopolyPlayer.moveBy(steps: Int): Unit {
        this.moveTo(this.position + steps, true)
    }

    fun sendToJail(player: MonopolyPlayer): Unit {
        player.moveTo(board.jailIdx)
        player.jailTurns = params.jailDiceRolls
    }

    /**
     * Returns the tile that this player is currently standing on
     */
    private fun MonopolyPlayer.getTile(): AbstractTile = board.tileset[this.position]

    fun purchaseTile(player: MonopolyPlayer): Boolean = runBlocking {
        val tile: AbstractTile = player.getTile()
        if (tile !is Purchasable) {
            sendMessage("This tile is not purchasable")
            return@runBlocking false
        }
        if (player.position in player.owns) {
            sendMessage("${player.name} already owns this tile")
            return@runBlocking false
        }
        if (player.money < tile.price) {
            sendMessage("Not enough money to buy this tile")
            return@runBlocking false
        }
        // Else, the player can buy this tile
        player.owns[player.position] = tile
        player.deductMoney(tile.price)
        return@runBlocking true
    }

    override fun getDetailedGameString(): String {
        return "Turn: $turn + $ply ply \n" +
        playerList.map { e: Map.Entry<Int, MonopolyPlayer> ->
            val p: MonopolyPlayer = e.value
            "#${e.key}: ${p.name} (${p.money} $COIN + ${p.owns.size} tiles) @ ${board.tileset[p.position].name}"
        }.joinToString("\n")
    }

    fun fetchPlayer(user: User): MonopolyPlayer? {
        return playerList.values.firstOrNull { player: MonopolyPlayer -> user.id.value == player.uid }
    }

    companion object {
        fun notifyNegBalance(player: MonopolyPlayer): String =
            "${player.name}'s balance is negative at \$${player.money}, " +
            "resolve debts before ending turn or declare bankruptcy"
    }
}