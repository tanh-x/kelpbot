package game

object GameManager {
    private val gamesList: MutableMap<ULong, AbstractGame> = mutableMapOf()

    fun forceRemoveGame(channelId: ULong): Unit {
        gamesList.remove(channelId)
    }

    fun hasGame(channelId: ULong): Boolean = channelId in gamesList

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