package game.monopoly_game.data

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer
import kotlin.math.min

enum class TileEffects(val effect: (p: MonopolyPlayer, gs: MonopolyGame) -> Unit = { _, _ -> }) {
    GO,  // Go tile effect already handled by MonopolyGame.moveTo()
    PARKING,  // Parking does nothing
    CHEST,
    CHANCE,
    TAX({ p: MonopolyPlayer, gs: MonopolyGame ->
        p.deductMoney(min(p.money / 10, 200))
    }),
    GO_TO_JAIL({ p: MonopolyPlayer, gs: MonopolyGame ->
        gs.sendToJail(p)
        gs.incrementPly()
    }),
}