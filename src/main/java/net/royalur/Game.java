package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.standard.StandardPiece;
import net.royalur.rules.standard.StandardRuleSetProvider;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A game of the Royal Game of Ur. Provides methods to allow the playing of games,
 * and methods to support the retrieval of history about the moves that were made.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public interface Game<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * Gets the set of rules that are being used for this game.
     * @return The set of rules that are being used for this game.
     */
    @Nonnull RuleSet<P, S, R> getRules();

    /**
     * Gets the dice to are used to make dice rolls.
     * @return The dice to be used to make dice rolls.
     */
    @Nonnull Dice<R> getDice();

    /**
     * Sets the value of the metadata associated with the key {@code key} to {@code value}.
     * @param key   The key of the metadata item to be updated.
     * @param value The new value to be associated with the given metadata key.
     */
    void putMetadata(@Nonnull String key, @Nonnull String value);

    /**
     * Removes any metadata associated with the key {@code key}.
     * @param key   The key of the metadata item to be removed.
     */
    void removeMetadata(@Nonnull String key);

    /**
     * Retrieves the metadata associated with the key {@code key}.
     * @param key The key of the metadata item to be retrieved.
     * @return The metadata associated with the key {@code key}.
     * @throws IllegalStateException If there is no metadata associated with the key {@code key}.
     */
    @Nonnull String getMetadata(@Nonnull String key);

    /**
     * Retrieves all metadata about this game.
     * @return The metadata about this game.
     */
    @Nonnull Map<String, String> getMetadata();

    /**
     * Retrieves the states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     * @return The states that have occurred so far in the game.
     */
    @Nonnull List<GameState<P, S, R>> getStates();

    /**
     * Retrieve the state that the game is currently in.
     * @return The state that the game is currently in.
     */
    @Nonnull GameState<P, S, R> getCurrentState();

    /**
     * Generates a copy of this game.
     * @return A copy of {@code this}.
     */
    @Nonnull Game<P, S, R> copy();

    /**
     * Rolls the dice, with a known value of {@code roll}, and updates the
     * state of the game accordingly.
     * @param roll The value of the dice that is to be rolled.
     */
    void rollDice(@Nonnull R roll);

    /**
     * Rolls the dice, and updates the state of the game accordingly.
     * @return The value of the dice that were rolled.
     */
    @Nonnull R rollDice();

    /**
     * Rolls the dice with a known value of {@code value}, and updates the state of the game accordingly.
     * @param value The value of the dice to be rolled.
     * @return The value of the dice that were rolled.
     */
    @Nonnull R rollDice(int value);

    /**
     * Finds all available moves that can be made from the current state of the game.
     * @return All available moves that can be made from the current state of the game.
     */
    @Nonnull List<Move<P>> findAvailableMoves();

    /**
     * Applies the move {@code move} to update the state of the game.
     * This does not check whether the move is valid.
     * @param move The move to make from the current state of the game.
     */
    void makeMove(@Nonnull Move<P> move);

    /**
     * Moves the piece {@code piece}, and updates the state of the game.
     * @param piece The piece to be moved.
     */
    void makeMove(@Nonnull P piece);

    /**
     * Moves a new piece onto the board.
     */
    void makeMoveIntroducingPiece();

    /**
     * Moves the piece on tile {@code tile}, and updates the state of the game.
     * @param tile The tile where the piece to be moved resides.
     */
    void makeMove(@Nonnull Tile tile);

    /**
     * Retrieves the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    default @Nonnull List<ActionGameState<P, S, R, ?>> getActionStates() {
        return getStates().stream()
                .filter(s -> s instanceof ActionGameState)
                .map(s -> (ActionGameState<P, S, R, ?>) s)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retrieves the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    default @Nonnull List<GameState<P, S, R>> getLandmarkStates() {
        List<GameState<P, S, R>> states = getStates();
        List<GameState<P, S, R>> landmarkStates = new ArrayList<>();
        for (int index = 0; index < states.size(); ++index) {
            GameState<P, S, R> state = states.get(index);
            if (state instanceof MovedGameState || index == states.size() - 1) {
                landmarkStates.add(state);
            }
        }
        return Collections.unmodifiableList(landmarkStates);
    }

    /**
     * Determines whether the game is currently in a finished state.
     * @return Whether the game is currently in a finished state.
     */
    default boolean isFinished() {
        return getCurrentState() instanceof WinGameState;
    }

    /**
     * Determines whether the game is currently in a playable state.
     * @return Whether the game is currently in a playable state.
     */
    default boolean isPlayable() {
        return getCurrentState() instanceof PlayableGameState;
    }

    /**
     * Retrieves the current state of this game as an instance {@link PlayableGameState}.
     * This will throw an error if the game is not in a playable state.
     * @return The playable state that the game is currently in.
     */
    default @Nonnull PlayableGameState<P, S, R, ?> getCurrentPlayableState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof PlayableGameState)
            return (PlayableGameState<P, S, R, ?>) state;

        throw new IllegalStateException("This game is not in a playable game state");
    }

    /**
     * Determines whether the game is currently in a state that is waiting for a roll from a player.
     * @return Whether the game is currently in a state that is waiting for a roll from a player.
     */
    default boolean isWaitingForRoll() {
        return getCurrentState() instanceof WaitingForRollGameState;
    }

    /**
     * Retrieves the current state of this game as an instance of {@link WaitingForRollGameState}.
     * This will throw an error if the game is not in a state that is waiting for a roll from a player.
     * @return The waiting for roll state that the game is currently in.
     */
    default @Nonnull WaitingForRollGameState<P, S, R> getCurrentWaitingForRollState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WaitingForRollGameState)
            return (WaitingForRollGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not waiting for a roll");
    }

    /**
     * Determines whether the game is currently in a state that is waiting for a move from a player.
     * @return Whether the game is currently in a state that is waiting for a move from a player.
     */
    default boolean isWaitingForMove() {
        return getCurrentState() instanceof WaitingForMoveGameState;
    }

    /**
     * Retrieves the current state of this game as an instance of {@link WaitingForMoveGameState}.
     * This will throw an error if the game is not in a state that is waiting for a move from a player.
     * @return The waiting for move state that the game is currently in.
     */
    default @Nonnull WaitingForMoveGameState<P, S, R> getCurrentWaitingForMoveState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WaitingForMoveGameState)
            return (WaitingForMoveGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not waiting for a move");
    }

    /**
     * Determines whether the game has finished.
     * @return Whether the game has finished.
     */
    default boolean hasEnded() {
        return getCurrentState() instanceof WinGameState;
    }

    /**
     * Retrieves the current state of this game as an instance of {@link WinGameState}.
     * This will throw an error if the game has not ended.
     * @return The win state that the game is currently in.
     */
    default @Nonnull WinGameState<P, S, R> getCurrentWinState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WinGameState)
            return (WinGameState<P, S, R>) state;

        throw new IllegalStateException("This game has not ended");
    }

    /**
     * Retrieves the state of the board in the current state of the game.
     * @return The state of the board in the current state of the game.
     */
    default @Nonnull Board<P> getBoard() {
        return getCurrentState().getBoard();
    }

    /**
     * Retrieves the current state of the light player.
     * @return The current state of the light player.
     */
    default @Nonnull S getLightPlayer() {
        return getCurrentState().getLightPlayer();
    }

    /**
     * Retrieves the current state of the dark player.
     * @return The current state of the dark player.
     */
    default @Nonnull S getDarkPlayer() {
        return getCurrentState().getDarkPlayer();
    }

    /**
     * Retrieves the state of the player whose turn it is.
     * @return The state of the player whose turn it is.
     */
    default @Nonnull S getTurnPlayer() {
        return getCurrentPlayableState().getTurnPlayer();
    }

    /**
     * Retrieves the state of the player that is waiting as it is not their turn.
     * @return The state of the player that is waiting as it is not their turn.
     */
    default @Nonnull S getWaitingPlayer() {
        return getCurrentPlayableState().getWaitingPlayer();
    }

    /**
     * Retrieves the player that won the game.
     * @return The player that won the game.
     */
    default PlayerType getWinner() {
        return getCurrentWinState().getWinner();
    }

    /**
     * Retrieves the player that lost the game.
     * @return The player that lost the game.
     */
    default PlayerType getLoser() {
        return getCurrentWinState().getLoser();
    }

    /**
     * Retrieves the state of the winning player.
     * @return The state of the winning player.
     */
    default @Nonnull S getWinningPlayer() {
        return getCurrentWinState().getWinningPlayer();
    }

    /**
     * Retrieves the state of the losing player.
     * @return The state of the losing player.
     */
    default @Nonnull S getLosingPlayer() {
        return getCurrentWinState().getLosingPlayer();
    }

    /**
     * Retrieves the roll that was made that can be used by the
     * current turn player to make a move.
     * @return The roll that was made that can now be used to make a move.
     */
    default @Nonnull R getRoll() {
        return getCurrentWaitingForMoveState().getRoll();
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     * @param settings The settings to initialise the builder with.
     * @return A builder to assist in constructing games with custom settings.
     */
    static <R extends Roll> @Nonnull GameBuilder<StandardPiece, PlayerState, R>
    builder(GameSettings<R> settings) {
        return new GameBuilder<>(
                settings, Collections.emptyMap(),
                new StandardRuleSetProvider()
        );
    }

    /**
     * Creates a standard game with custom settings.
     * @param settings The settings to use for the game.
     * @return A game with custom settings.
     */
    static <R extends Roll> @Nonnull Game<StandardPiece, PlayerState, R>
    create(GameSettings<R> settings) {
        return builder(settings).build();
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     * Provides a builder that is set up to produce games using the Finkel
     * rule set by default.
     * @return A builder to assist in constructing games with custom settings.
     */
    static @Nonnull GameBuilder<StandardPiece, PlayerState, Roll> builder() {
        return builder(GameSettings.FINKEL);
    }

    /**
     * Creates a standard game that follows the default rules on RoyalUr.net. Therefore,
     * this uses the simple rules, the standard board shape, Bell's path, the standard dice,
     * and seven starting pieces per player.
     * @return A standard game.
     */
    static @Nonnull Game<StandardPiece, PlayerState, Roll> createRoyalUrNet() {
        return builder(GameSettings.ROYALUR_NET).build();
    }

    /**
     * Creates a standard game that follows the rules proposed by Irving Finkel.
     * This uses the simple rules, the standard board shape, Bell's path, safe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    static @Nonnull Game<StandardPiece, PlayerState, Roll> createFinkel() {
        return builder(GameSettings.FINKEL).build();
    }

    /**
     * Creates a standard game that follows the rules proposed by James Masters.
     * This uses the simple rules, the standard board shape, Bell's path, unsafe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    static @Nonnull Game<StandardPiece, PlayerState, Roll> createMasters() {
        return builder(GameSettings.MASTERS).build();
    }

    /**
     * Creates a game of Aseb. This uses the simple rules, the Aseb board shape,
     * the Aseb paths, the standard dice, and five starting pieces per player.
     * @return A game of Aseb.
     */
    static @Nonnull Game<StandardPiece, PlayerState, Roll> createAseb() {
        return builder(GameSettings.ASEB).build();
    }
}
