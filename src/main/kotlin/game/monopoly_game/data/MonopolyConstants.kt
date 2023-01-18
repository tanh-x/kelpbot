package game.monopoly_game.data

object MonopolyConstants {
    const val NUMBER_OF_DICE: Int = 2
    const val NUMBER_OF_FACES: Int = 6

    const val STARTING_BALANCE: Int = 500
    const val PASS_GO_REWARD: Int = 200
    const val JAIL_FINE: Int = 50
    const val JAIL_DICE_ROLLS: Int = 3
    const val COLOR_SET_MULTIPLIER: Float = 2f
    const val MAX_DOUBLE_ROLLS: Int = 2

    @JvmStatic
    val UTILITY_DICE_MULTIPLIERS: Array<Float> = arrayOf(0f, 4f, 10f, 18f, 27f, 37f, 49f, 63f)

    @JvmStatic
    val STD_UTILITY_POSITIONS: Array<Int> = arrayOf(12, 28)

    @JvmStatic
    val STD_RAILROAD_POSITIONS: Array<Int> = arrayOf(15, 25, 35)

    const val STD_BOARD_SIZE: Int = 40

    const val STD_BOARD_CSV_PATH: String = "src/main/kotlin/game/monopoly_game/data/board-standard.csv"
    const val MAX_BOARD_CSV_FILE_SIZE: Int = 16 * 1024
    const val MAX_TILES: Int = 200
}