package net.royalur.model;

/**
 * Represents the purpose of each game state.
 */
public enum GameStateType {

    /**
     * A state that is included just for information,
     * but which cannot be played from.
     */
    INFO,

    /**
     * A state where a player is yet to make a move.
     */
    WAITING_FOR_MOVE,

    /**
     * A state where a player is yet to roll the dice.
     */
    WAITING_FOR_ROLL,

    /**
     * A state where a player has won.
     */
    WIN,

    /**
     * Reserved for custom game states.
     */
    CUSTOM
}
