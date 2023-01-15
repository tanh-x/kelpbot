package game

interface TurnBasedGame {
    val turn: Int
    val ply: Int
}