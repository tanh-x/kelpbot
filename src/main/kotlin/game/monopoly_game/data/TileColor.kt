package game.monopoly_game.data

data class TileColor(
    val buildPrice: Int,
    val label: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other || javaClass != other?.javaClass) return true
        return (label.contentEquals((other as TileColor).label))
    }

    override fun hashCode(): Int = 31 * buildPrice + label.hashCode()
}


//enum class TileColor(val buildPrice: Int, val tiles: Array<Int>) {
//    BROWN(50, arrayOf(1, 3)),
//    CYAN(50, arrayOf(6, 8, 9)),
//    PINK(100, arrayOf(11, 13, 14)),
//    ORANGE(100, arrayOf(16, 18, 19)),
//    RED(150, arrayOf(21, 23, 24)),
//    YELLOW(150, arrayOf(26, 27, 29)),
//    GREEN(200, arrayOf(31, 32, 34)),
//    BLUE(200, arrayOf(37, 39))
//}