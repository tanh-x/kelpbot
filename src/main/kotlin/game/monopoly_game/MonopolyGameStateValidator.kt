package game.monopoly_game

import game.monopoly_game.board.AbstractTile
import game.monopoly_game.board.Purchasable

object MonopolyGameStateValidator {
    fun validateGame(game: MonopolyGame, onlyReturnFails: Boolean = true): Map<ValidationChecks, Boolean> {
        val result: MutableMap<ValidationChecks, Boolean> = mutableMapOf()
        ValidationChecks.values().map { check: ValidationChecks ->
            result.put(check, check.eval(game))
        }
        return if (onlyReturnFails) (result.filterValues { r -> !r }) else (result)
    }
}

enum class ValidationChecks(
    val description: String,
    val eval: (MonopolyGame) -> Boolean,
) {
    OWNERSHIP_CONSISTENCY(
        "Ownership claims made by MonopolyPlayer instances and Purchasable instances must be consistent",
        { game: MonopolyGame ->
            val ownershipPlayers: Map<MonopolyPlayer, MutableSet<Int>> = game.playerList.values
                .associateWith { player: MonopolyPlayer -> player.owns.keys.toMutableSet() }

            val ownershipBoard: MutableMap<MonopolyPlayer, MutableSet<Int>> = game.playerList.values
                .associateWith { _: MonopolyPlayer -> mutableSetOf<Int>() }.toMutableMap()

            game.board.tileset.forEach { tile: AbstractTile ->
                if (tile !is Purchasable || tile.owner == null) return@forEach
                ownershipBoard[tile.owner]!!.add(tile.position)
            }

            ownershipBoard == ownershipPlayers
        }
    )

    // TODO: Negative money check

    // TODO: jailTurns is in -1 and params.jailDiceRolls check

    // TODO:
}