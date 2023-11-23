package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A game of the Royal Game of Ur. Provides methods to allow the playing of games,
 * and methods to support the retrieval of history about the moves that were made.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public class Game<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The set of rules that are being used for this game.
     */
    private final @Nonnull RuleSet<P, S, R> rules;

    /**
     * The metadata of this game.
     */
    private final @Nonnull GameMetadata metadata;

    /**
     * The dice to be used to generate dice rolls.
     */
    private final @Nonnull Dice<R> dice;

    /**
     * The states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     */
    private final @Nonnull List<GameState<P, S, R>> states;

    /**
     * Instantiates a game of the Royal Game of Ur.
     * @param rules The set of rules that are being used for this game.
     * @param metadata The metadata of this game.
     * @param states The states that have occurred so far in the game.
     */
    public Game(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull GameMetadata metadata,
            @Nonnull List<GameState<P, S, R>> states
    ) {
        if (states.isEmpty())
            throw new IllegalArgumentException("Games must have at least one state");

        this.rules = rules;
        this.metadata = metadata;
        this.dice = rules.getDiceFactory().createDice();
        this.states = new ArrayList<>();

        addStates(states);
    }

    /**
     * Instantiates a game of the Royal Game of Ur that has not yet had any moves played.
     * @param rules The rules of the game.
     */
    public Game(@Nonnull RuleSet<P, S, R> rules) {
        this(
                rules,
                GameMetadata.createForNewGame(rules.getSettings()),
                List.of(rules.generateInitialGameState())
        );
    }

    /**
     * Instantiates a game of the Royal Game of Ur that is a copy of {@code game}.
     * @param game The rules of the game.
     */
    protected Game(@Nonnull Game<P, S, R> game) {
        this(game.rules, game.metadata.copy(), game.states);
        dice.copyFrom(game.dice);
    }

    /**
     * Generates a copy of this game.
     * @return A copy of {@code this}.
     */
    public @Nonnull Game<P, S, R> copy() {
        if (!getClass().equals(Game.class)) {
            throw new UnsupportedOperationException(
                    getClass() + " does not support copy"
            );
        }
        return new Game<>(this);
    }

    /**
     * Adds all states from {@code states} to this game.
     * @param states The states to add to this game.
     */
    public void addStates(@Nonnull Iterable<GameState<P, S, R>> states) {
        int seen = 0;
        for (GameState<P, S, R> state : states) {
            seen += 1;
            if (state == null)
                throw new IllegalArgumentException("The states list should not contain any null entries");

            addState(state);
        }
        if (seen == 0)
            throw new IllegalArgumentException("There were no states to add");
    }

    /**
     * Adds the state {@code state} to this game.
     * @param state The state to add to this game.
     */
    public void addState(@Nonnull GameState<P, S, R> state) {
        states.add(state);
    }

    /**
     * Gets the set of rules that are being used for this game.
     * @return The set of rules that are being used for this game.
     */
    public @Nonnull RuleSet<P, S, R> getRules() {
        return rules;
    }

    /**
     * Gets the metadata of this game.
     * @return The metadata of this game.
     */
    public @Nonnull GameMetadata getMetadata() {
        return metadata;
    }

    /**
     * Gets the dice to are used to make dice rolls.
     * @return The dice to be used to make dice rolls.
     */
    public @Nonnull Dice<R> getDice() {
        return dice;
    }

    /**
     * Gets the states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     * @return The states that have occurred so far in the game.
     */
    public @Nonnull List<GameState<P, S, R>> getStates() {
        return List.copyOf(states);
    }

    /**
     * Retrieve the state that the game is currently in.
     * @return The state that the game is currently in.
     */
    public @Nonnull GameState<P, S, R> getCurrentState() {
        return states.get(states.size() - 1);
    }

    /**
     * Gets the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    public @Nonnull List<ActionGameState<P, S, R>> getActionStates() {
        return states.stream()
                .filter(s -> s instanceof ActionGameState)
                .map(s -> (ActionGameState<P, S, R>) s)
                .toList();
    }

    /**
     * Gets all moves that were made in the game, as well as
     * the current state of the game. These states are considered
     * landmark states as they contain all the information required
     * to recreate everything that happened in the game so far.
     */
    public @Nonnull List<GameState<P, S, R>> getLandmarkStates() {
        return rules.selectLandmarkStates(states);
    }

    /**
     * Determines whether the game is currently in a playable state.
     * @return Whether the game is currently in a playable state.
     */
    public boolean isPlayable() {
        return getCurrentState() instanceof PlayableGameState;
    }

    /**
     * Determines whether the game is currently waiting for a roll from a player.
     * @return Whether the game is currently waiting for a roll from a player.
     */
    public boolean isWaitingForRoll() {
        return getCurrentState() instanceof WaitingForRollGameState;
    }

    /**
     * Determines whether the game is currently waiting for a move from a player.
     * @return Whether the game is currently waiting for a move from a player.
     */
    public boolean isWaitingForMove() {
        return getCurrentState() instanceof WaitingForMoveGameState;
    }

    /**
     * Determines whether the game is currently in a finished state.
     * @return Whether the game is currently in a finished state.
     */
    public boolean isFinished() {
        return getCurrentState() instanceof WinGameState;
    }

    /**
     * Gets the current state of this game as a {@link PlayableGameState}.
     * This will throw an error if the game is not in a playable state.
     * @return The playable state that the game is currently in.
     */
    public @Nonnull PlayableGameState<P, S, R> getCurrentPlayableState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof PlayableGameState)
            return (PlayableGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not in a playable game state");
    }

    /**
     * Gets the current state of this game as an instance of {@link WaitingForRollGameState}.
     * This will throw an error if the game is not waiting for a roll from a player.
     * @return The waiting for roll state that the game is currently in.
     */
    public @Nonnull WaitingForRollGameState<P, S, R> getCurrentWaitingForRollState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WaitingForRollGameState)
            return (WaitingForRollGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not waiting for a roll");
    }

    /**
     * Gets the current state of this game as an instance of {@link WaitingForMoveGameState}.
     * This will throw an error if the game is not waiting for a move from a player.
     * @return The waiting for move state that the game is currently in.
     */
    public @Nonnull WaitingForMoveGameState<P, S, R> getCurrentWaitingForMoveState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WaitingForMoveGameState)
            return (WaitingForMoveGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not waiting for a move");
    }

    /**
     * Gets the current state of this game as an instance of {@link WinGameState}.
     * This will throw an error if the game has not ended.
     * @return The win state that the game is currently in.
     */
    public @Nonnull WinGameState<P, S, R> getCurrentWinState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WinGameState)
            return (WinGameState<P, S, R>) state;

        throw new IllegalStateException("This game has not ended");
    }

    /**
     * Rolls the dice, with a known value of {@code roll}, and updates the
     * state of the game accordingly.
     * @param roll The value of the dice that is to be rolled.
     */
    public void rollDice(@Nonnull R roll) {
        WaitingForRollGameState<P, S, R> state = getCurrentWaitingForRollState();
        addStates(rules.applyRoll(state, roll));
    }

    /**
     * Rolls the dice, and updates the state of the game accordingly.
     * @return The value of the dice that were rolled.
     */
    public @Nonnull R rollDice() {
        R roll = dice.roll();
        rollDice(roll);
        return roll;
    }

    /**
     * Rolls the dice with a known value of {@code value}, and updates
     * the state of the game accordingly.
     * @param value The value of the dice to be rolled.
     * @return The value of the dice that were rolled.
     */
    public @Nonnull R rollDice(int value) {
        R roll = dice.roll(value);
        rollDice(roll);
        return roll;
    }

    /**
     * Finds all moves that can be made from the current position.
     * @return All moves that can be made from the current position.
     */
    public @Nonnull List<Move<P>> findAvailableMoves() {
        return getCurrentWaitingForMoveState().getAvailableMoves();
    }

    /**
     * Applies the move {@code move} to update the state of the game.
     * This does not check whether the move is valid.
     * @param move The move to make from the current state of the game.
     */
    public void makeMove(@Nonnull Move<P> move) {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        addStates(rules.applyMove(state, move));
    }

    /**
     * Moves the piece {@code piece}, and updates the state of the game.
     * @param piece The piece to be moved.
     */
    public void makeMove(@Nonnull P piece) {
        for (Move<P> move : findAvailableMoves()) {
            if (!move.hasSource() || !move.getSourcePiece().equals(piece))
                continue;

            makeMove(move);
            return;
        }
        throw new IllegalStateException("The piece cannot be moved, " + piece);
    }

    /**
     * Moves the piece on the given source tile, and updates the state of the game.
     * @param sourceTile The tile where the piece to be moved resides.
     */
    public void makeMove(@Nonnull Tile sourceTile) {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        PathPair paths = rules.getPaths();
        for (Move<P> move : findAvailableMoves()) {
            if (!move.getSource(paths).equals(sourceTile))
                continue;

            makeMove(move);
            return;
        }
        throw new IllegalStateException("There is no available move from " + sourceTile);
    }

    /**
     * Moves a new piece onto the board.
     */
    public void makeMoveIntroducingPiece() {
        for (Move<P> move : findAvailableMoves()) {
            if (!move.isIntroducingPiece())
                continue;

            makeMove(move);
            return;
        }
        throw new IllegalStateException("There is no available move to introduce a piece to the board");
    }

    /**
     * Gets the current state of the board.
     * @return The current state of the board.
     */
    public @Nonnull Board<P> getBoard() {
        return getCurrentState().getBoard();
    }

    /**
     * Gets the current state of the light player.
     * @return The current state of the light player.
     */
    public @Nonnull S getLightPlayer() {
        return getCurrentState().getLightPlayer();
    }

    /**
     * Gets the current state of the dark player.
     * @return The current state of the dark player.
     */
    public @Nonnull S getDarkPlayer() {
        return getCurrentState().getDarkPlayer();
    }

    /**
     * Gets the current state of the player {@code player}.
     * @param player The player to get the state of.
     * @return The state of the player {@code player}.
     */
    public @Nonnull S getPlayer(@Nonnull PlayerType player) {
        return getCurrentState().getPlayer(player);
    }

    /**
     * Gets the player who can make the next interaction with the game.
     * @return The player who can make the next interaction with the game.
     */
    public @Nonnull PlayerType getTurn() {
        return getCurrentPlayableState().getTurn();
    }

    /**
     * Gets the player who can make the next interaction with the game,
     * or the winner of the game if it is finished.
     * @return The player who can make the next interaction with the game,
     *         or the winner of the game if it is finished.
     */
    public @Nonnull PlayerType getTurnOrWinner() {
        if (isPlayable())
            return getTurn();

        if (isFinished())
            return getWinner();

        throw new IllegalStateException("The game is not in a playable or won state");
    }

    /**
     * Gets the state of the player whose turn it is.
     * @return The state of the player whose turn it is.
     */
    public @Nonnull S getTurnPlayer() {
        return getCurrentPlayableState().getTurnPlayer();
    }

    /**
     * Gets the state of the player that is waiting as it is not their turn.
     * @return The state of the player that is waiting as it is not their turn.
     */
    public @Nonnull S getWaitingPlayer() {
        return getCurrentPlayableState().getWaitingPlayer();
    }

    /**
     * Gets the player that won the game.
     * @return The player that won the game.
     */
    public @Nonnull PlayerType getWinner() {
        return getCurrentWinState().getWinner();
    }

    /**
     * Gets the player that lost the game.
     * @return The player that lost the game.
     */
    public @Nonnull PlayerType getLoser() {
        return getCurrentWinState().getLoser();
    }

    /**
     * Gets the state of the winning player.
     * @return The state of the winning player.
     */
    public @Nonnull S getWinningPlayer() {
        return getCurrentWinState().getWinningPlayer();
    }

    /**
     * Gets the state of the losing player.
     * @return The state of the losing player.
     */
    public @Nonnull S getLosingPlayer() {
        return getCurrentWinState().getLosingPlayer();
    }

    /**
     * Gets the roll that was made that can be used by the
     * current turn player to make a move.
     * @return The roll that was made that can now be used to make a move.
     */
    public @Nonnull R getRoll() {
        return getCurrentWaitingForMoveState().getRoll();
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     */
    public static @Nonnull GameBuilder<Piece, PlayerState, Roll> builder() {
        return new GameBuilder<>(GameSettings.FINKEL, new SimpleRuleSetProvider());
    }

    /**
     * Creates a simple game with custom settings.
     * @param settings The settings to use for the game.
     * @return A game with custom settings.
     */
    public static <R extends Roll> @Nonnull Game<Piece, PlayerState, R>
    create(GameSettings<R> settings) {
        return builder().replaceSettings(settings).build();
    }

    /**
     * Creates a game that follows the rules proposed by Irving Finkel.
     * This uses the simple rules, the standard board shape, Bell's path, safe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    public static @Nonnull Game<Piece, PlayerState, Roll> createFinkel() {
        return create(GameSettings.FINKEL);
    }

    /**
     * Creates a game that follows the rules proposed by James Masters.
     * This uses the simple rules, the standard board shape, Bell's path, unsafe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    public static @Nonnull Game<Piece, PlayerState, Roll> createMasters() {
        return create(GameSettings.MASTERS);
    }

    /**
     * Creates a game of Aseb. This uses the simple rules, the Aseb board shape,
     * the Aseb paths, the standard dice, and five starting pieces per player.
     * @return A game of Aseb.
     */
    public static @Nonnull Game<Piece, PlayerState, Roll> createAseb() {
        return create(GameSettings.ASEB);
    }
}
