package game.game_interfaces

import kotlin.random.Random

interface DiceGame {
    val diceRoll: ArrayList<Int>
    val diceSum: Int
        get() = diceRoll.sum()

    fun roll(numDice: Int, numFace: Int): ArrayList<Int> {
        diceRoll.clear()
        repeat(numDice) { diceRoll.add(Random.nextInt(numFace) + 1) }
        return diceRoll
    }

    fun wasDoubleRoll(): Boolean = diceRoll.toSet().size == 1
}