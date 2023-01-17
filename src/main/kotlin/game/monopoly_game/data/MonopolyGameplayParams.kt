package game.monopoly_game.data

data class MonopolyGameplayParams(
    val numberOfDice: Int = MonopolyConstants.NUMBER_OF_DICE,
    val numberOfFaces: Int = MonopolyConstants.NUMBER_OF_FACES,
    val startingBalance: Int = MonopolyConstants.STARTING_BALANCE,
    val passGoReward: Int = MonopolyConstants.PASS_GO_REWARD,
    val jailFine: Int = MonopolyConstants.JAIL_FINE,
    val jailDiceRolls: Int = MonopolyConstants.JAIL_DICE_ROLLS,
    val colorSetMultiplier: Float = MonopolyConstants.COLOR_SET_MULTIPLIER,
    val maxDoubleRolls: Int = MonopolyConstants.MAX_DOUBLE_ROLLS,
    val utilityDiceMultipliers: Array<Float> = MonopolyConstants.UTILITY_DICE_MULTIPLIERS
)