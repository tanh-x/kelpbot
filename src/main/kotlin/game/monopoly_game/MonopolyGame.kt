package game.monopoly_game

import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.interfaces.DiceGame
import game.interfaces.TurnBasedGame

class MonopolyGame(
    playerList: MutableSet<MonopolyPlayer>,
    hostChannel: MessageChannel,
) : AbstractGame(
    playerList.map { p: MonopolyPlayer -> p.member }.toMutableSet(),
    hostChannel
), TurnBasedGame, DiceGame {
    override var turn: Int = 0
    override var ply: Int = 0
    override val playerTurns: MutableMap<Int, Member> = playerList
        .mapIndexed { i: Int, p: MonopolyPlayer -> Pair(i, p.member) }
        .toMap(mutableMapOf())
    override val diceRoll: ArrayList<Int> = arrayListOf()


}