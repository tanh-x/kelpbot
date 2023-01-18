package utils

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer
import game.monopoly_game.board.AbstractTile
import game.monopoly_game.board.JailTile
import game.monopoly_game.board.MonopolyBoard
import game.monopoly_game.board.RailroadTile
import game.monopoly_game.board.SpecialTile
import game.monopoly_game.board.StandardTile
import game.monopoly_game.board.UtilityTile
import game.monopoly_game.data.MonopolyConstants
import game.monopoly_game.data.TileColor
import java.io.File
import java.lang.IllegalArgumentException

fun instantiateStandardBoard(): MonopolyBoard = instantiateBoardFromCSV(
    File(MonopolyConstants.STD_BOARD_CSV_PATH)
)

fun instantiateBoardFromCSV(srcFile: File): MonopolyBoard {
    if (srcFile.length() > MonopolyConstants.MAX_BOARD_CSV_FILE_SIZE) {
        throw SecurityException("CSV file too large")
    }

    val lines: List<String> = srcFile.readLines().drop(1)
    if (lines.size > MonopolyConstants.MAX_TILES) {
        throw IllegalArgumentException("Too many tiles (${lines.size} > ${MonopolyConstants.MAX_TILES})")
    }

    val tileset: ArrayList<AbstractTile> = ArrayList()
    val colorSets: MutableMap<TileColor, ArrayList<Int>> = mutableMapOf()
    val noneColor: TileColor = TileColor(-1, "None")
    lines.forEachIndexed { idx: Int, s: String ->
        val args: List<String> = s.split(",")

        if (args[0].toIntOrNull() != idx)
            throw IllegalArgumentException("Position column doesn't match order")

        val color: TileColor
        if (args[5] != "None") {
            color = TileColor(
                buildPrice = args[5].toIntOrNull()
                    ?: throw IllegalArgumentException("Color column isn't an integer on row $idx"),
                label = args[3]
            )
            colorSets.getOrPut(color) { arrayListOf() }.add(idx)
        } else {
            color = noneColor
        }


        val tilePrice: Int = args[4].toIntOrNull()
            ?: throw IllegalArgumentException("Price column isn't an integer on row $idx")
        val tilePayout: Array<Int> = args.subList(6, 11).map { rentPayout: String ->
            rentPayout.toIntOrNull()
                ?: throw IllegalArgumentException("A payout colum isn't an integer on row $idx")
        }.toTypedArray()

        val newTile: AbstractTile = when (args[2]) {
            "Street" -> StandardTile(
                id = idx,
                name = args[1],
                color = color,
                price = tilePrice,
                payout = tilePayout
            )

            "Railroad" -> RailroadTile(
                id = idx,
                name = args[1],
                price = tilePrice,
                payout = tilePayout
            )

            "Utility" -> UtilityTile(
                id = idx,
                name = args[1],
                price = tilePrice,
                payout = tilePayout
            )

            "Jail" -> JailTile(
                id = idx,
                name = args[1]
            )

            else -> SpecialTile(
                id = idx,
                name = args[1],
                effect = when (args[2]) {
                    "Go" -> TileEffects.GO.effect
                    "Parking" -> TileEffects.PARKING.effect
                    "Chest" -> TileEffects.CHEST.effect
                    "Chance" -> TileEffects.CHANCE.effect
                    "Tax" -> TileEffects.TAX.effect
                    "GoToJail" -> TileEffects.GO_TO_JAIL.effect
                    else -> {
                        throw IllegalArgumentException("Unknown type: ${args[2]} on row $idx")
                    }
                }
            )
        }

        tileset.add(newTile)
    }

    return MonopolyBoard(
        tileset = tileset.toTypedArray(),
        colorSets = colorSets.mapValues { entry: Map.Entry<TileColor, ArrayList<Int>> ->
            entry.value.toTypedArray()
        }
    )
}

// TODO: Implement special tile effects
enum class TileEffects(val effect: (p: MonopolyPlayer, gs: MonopolyGame) -> Unit = { _, _ -> }) {
    GO,  // Go tile effect already handled by MonopolyGame.moveTo()
    PARKING,  // Parking does nothing
    CHEST,
    CHANCE,
    TAX,
    GO_TO_JAIL({ p: MonopolyPlayer, gs: MonopolyGame ->
        gs.sendToJail(p)
        gs.incrementPly()
    }),
}

