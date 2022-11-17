package net.royalur.model;

/**
 * A game state represents a single point within a game.
 */
public class GameState {

    public final Board board;

    public GameState(Board board) {
        this.board = board;
    }
}
