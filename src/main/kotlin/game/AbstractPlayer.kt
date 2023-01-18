package game

import dev.kord.core.entity.Member
import utils.getUID

abstract class AbstractPlayer(
    val member: Member,
    isHost: Boolean,
) {
    var isHost: Boolean = isHost
        protected set
    val name: String
        get() = member.displayName

    val uid: ULong
        get() = member.getUID()
}