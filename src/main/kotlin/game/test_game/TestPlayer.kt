package game.test_game

import dev.kord.core.entity.Member
import game.AbstractPlayer

class TestPlayer(
    member: Member,
    isHost: Boolean = false,
): AbstractPlayer(member, isHost) {
    var magicNumber: Int = 23
}