package game.monopoly_game.board

import game.monopoly_game.board.tile.AbstractTile
import game.monopoly_game.board.tile.JailTile
import game.monopoly_game.board.tile.RailroadTile
import game.monopoly_game.board.tile.SpecialTile
import game.monopoly_game.board.tile.StandardTile
import game.monopoly_game.board.tile.UtilityTile
import game.monopoly_game.data.MonopolyConstants
import game.monopoly_game.data.TileColor
import game.monopoly_game.data.TileEffects.CHANCE
import game.monopoly_game.data.TileEffects.CHEST
import game.monopoly_game.data.TileEffects.GO
import game.monopoly_game.data.TileEffects.GO_TO_JAIL
import game.monopoly_game.data.TileEffects.PARKING
import game.monopoly_game.data.TileEffects.TAX
import java.io.File

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
    val shorthands: MutableMap<String, Int> = mutableMapOf()
    val noneColor: TileColor = TileColor(-1, "None")
    lines.forEachIndexed { index: Int, s: String ->
        val args: List<String> = s.split(",")

        if (args[0].toIntOrNull() != index)
            throw IllegalArgumentException("Position column doesn't match order")

        if (args[2] in shorthands) throw IllegalArgumentException("Duplicate identifier: ${args[2]}")

        val color: TileColor
        if (args[4] == "None") {
            color = noneColor
        } else {
            color = TileColor(
                buildPrice = args[6].toIntOrNull()
                    ?: throw IllegalArgumentException("Color column isn't an integer on row $index"),
                label = args[4]
            )
            colorSets.getOrPut(color) { arrayListOf() }.add(index)
        }


        val tilePrice: Int = args[5].toIntOrNull()
            ?: throw IllegalArgumentException("Price column isn't an integer on row $index")

        val tilePayout: Array<Int> = args.subList(7, 12).map { payout: String ->
            payout.toIntOrNull() ?: throw IllegalArgumentException("A payout column isn't an integer on row $index")
        }.toTypedArray()

        val newTile: AbstractTile = instantiateTile(
            index = index,
            name = args[1],
            shorthand = args[2],
            type = args[3],
            color = color,
            tilePrice = tilePrice,
            tilePayout = tilePayout
        )

        shorthands[args[2]] = index
        tileset.add(newTile)
    }

    return MonopolyBoard(
        tileset = tileset.toTypedArray(),
        colorSets = colorSets.mapValues { entry: Map.Entry<TileColor, ArrayList<Int>> ->
            entry.value.toTypedArray()
        },
        shorthands = shorthands.toMap()
    )
}

fun instantiateTile(
    index: Int,
    name: String,
    shorthand: String,
    type: String,
    color: TileColor,
    tilePrice: Int,
    tilePayout: Array<Int>,
): AbstractTile {
    return when (type) {
        "Street" -> StandardTile(
            id = index,
            name = name,
            shorthand = shorthand,
            color = color,
            price = tilePrice,
            payout = tilePayout
        )

        "Railroad" -> RailroadTile(
            id = index,
            name = name,
            shorthand = shorthand,
            price = tilePrice,
            payout = tilePayout
        )

        "Utility" -> UtilityTile(
            id = index,
            name = name,
            shorthand = shorthand,
            price = tilePrice,
            payout = tilePayout
        )

        "Jail" -> JailTile(
            id = index,
            name = name,
            shorthand = shorthand
        )

        else -> SpecialTile(
            position = index,
            name = name,
            shorthand = shorthand,
            effect = when (type.lowercase()) {
                "go" -> GO.effect
                "parking" -> PARKING.effect
                "chest" -> CHEST.effect
                "chance" -> CHANCE.effect
                "tax" -> TAX.effect
                "gotojail" -> GO_TO_JAIL.effect
                else -> throw IllegalArgumentException("Unknown type: $type on row $index")
            }
        )
    }
}