package game

import dev.kord.core.entity.channel.MessageChannel

object GameManager {
    private val gamesList: MutableMap<ULong, AbstractGame> = mutableMapOf()

    fun forciblyKillGame(channelId: ULong): Unit {
        gamesList.remove(channelId)
    }

    fun MessageChannel.hasGame(): Boolean = this.id.value in gamesList

    fun MessageChannel.getGame(): AbstractGame? = gamesList[this.id.value]
    fun getGame(channelId: ULong): AbstractGame? = gamesList[channelId]

    /**
     * @param channelId the ID of the channel to associate this game with
     * @param game The game instance
     * @return True if the [channelId] key is not already associated with a game, false if there is
     * already a game in this channel
     */
    fun createNewGame(channelId: ULong, game: AbstractGame): Boolean {
        gamesList.putIfAbsent(channelId, game).also { return it == null }
    }
}