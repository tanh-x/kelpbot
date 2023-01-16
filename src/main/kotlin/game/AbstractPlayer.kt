package game

import dev.kord.core.entity.Member

abstract class AbstractPlayer(
    val member: Member,
    isHost: Boolean,
) {
    var isHost: Boolean = isHost
        protected set
    val name: String
        get() = member.displayName

    val id: ULong
        get() = member.id.value
}