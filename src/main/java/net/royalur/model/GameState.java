package net.royalur.model;

import javax.annotation.Nonnull;

/**
 * A game state represents a single point within a game.
 */
public class GameState {

    public final @Nonnull Board board;

    public GameState(@Nonnull Board board) {
        this.board = board;
    }
}
