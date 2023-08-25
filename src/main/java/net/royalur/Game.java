package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
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
public class Game<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The set of rules that are being used for this game.
     */
    private final @Nonnull RuleSet<P, S, R> rules;

    /**
     * The dice to be used to generate dice rolls.
     */
    private final @Nonnull Dice<R> dice;

    /**
     * The metadata of this game.
     */
    private final @Nonnull GameMetadata metadata;

    /**
     * The states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     */
    private final @Nonnull List<GameState<P, S, R>> states;

    /**
     * Instantiates a game of the Royal Game of Ur.
     * @param rules The set of rules that are being used for this game.
     * @param states The states that have occurred so far in the game.
     * @param metadata The metadata of this game.
     */
    public Game(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull List<GameState<P, S, R>> states,
            @Nonnull GameMetadata metadata
    ) {
        if (states.isEmpty())
            throw new IllegalArgumentException("Games must have at least one state to play from");

        this.rules = rules;
        this.dice = rules.getDiceFactory().createDice();
        this.metadata = metadata;
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
                List.of(rules.generateInitialGameState()),
                GameMetadata.createForNewGame(rules)
        );
    }

    /**
     * Instantiates a game of the Royal Game of Ur that is a copy of {@code game}.
     * @param game The rules of the game.
     */
    protected Game(@Nonnull Game<P, S, R> game) {
        this(game.rules, game.states, game.metadata.copy());
        dice.copyFrom(game.dice);
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
        // Actually add the state to this game!
        states.add(state);
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
     * Gets the set of rules that are being used for this game.
     * @return The set of rules that are being used for this game.
     */
    public @Nonnull RuleSet<P, S, R> getRules() {
        return rules;
    }

    /**
     * Gets the dice to are used to make dice rolls.
     * @return The dice to be used to make dice rolls.
     */
    public @Nonnull Dice<R> getDice() {
        return dice;
    }

    /**
     * Retrieves the metadata of this game.
     * @return The metadata of this game.
     */
    public @Nonnull GameMetadata getMetadata() {
        return metadata;
    }

    /**
     * Retrieves the states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     * @return The states that have occurred so far in the game.
     */
    public @Nonnull List<GameState<P, S, R>> getStates() {
        return Collections.unmodifiableList(states);
    }

    /**
     * Retrieve the state that the game is currently in.
     * @return The state that the game is currently in.
     */
    public @Nonnull GameState<P, S, R> getCurrentState() {
        return states.get(states.size() - 1);
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
     * Rolls the dice with a known value of {@code value}, and updates the state of the game accordingly.
     * @param value The value of the dice to be rolled.
     * @return The value of the dice that were rolled.
     */
    public @Nonnull R rollDice(int value) {
        R roll = dice.generateRoll(value);
        rollDice(roll);
        return roll;
    }

    /**
     * Finds all available moves that can be made from the current state of the game.
     * @return All available moves that can be made from the current state of the game.
     */
    public @Nonnull List<Move<P>> findAvailableMoves() {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        return rules.findAvailableMoves(
                state.getBoard(),
                state.getTurnPlayer(),
                state.getRoll()
        );
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
            if (!move.getSourcePiece().equals(piece))
                continue;

            makeMove(move);
            return;
        }
        throw new IllegalStateException("The piece cannot be moved, " + piece);
    }

    /**
     * Moves the piece on tile {@code tile}, and updates the state of the game.
     * @param tile The tile where the piece to be moved resides.
     */
    public void makeMove(@Nonnull Tile tile) {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        Board<P> board = state.getBoard();

        if (!board.contains(tile)) {
            Tile startTile = rules.getPaths().getStart(state.getTurn());
            if (!tile.equals(startTile)) {
                throw new IllegalStateException(
                        "The tile does not exist on the board, " + tile +
                        ", and is not the source tile for introducing pieces"
                );
            }
            makeMoveIntroducingPiece();
            return;
        }

        P piece = board.get(tile);
        if (piece == null)
            throw new IllegalStateException("There is no piece on tile " + tile);

        makeMove(piece);
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
     * Retrieves the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    public @Nonnull List<ActionGameState<P, S, R, ?>> getActionStates() {
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
    public @Nonnull List<GameState<P, S, R>> getLandmarkStates() {
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
    public boolean isFinished() {
        return getCurrentState() instanceof WinGameState;
    }

    /**
     * Determines whether the game is currently in a playable state.
     * @return Whether the game is currently in a playable state.
     */
    public boolean isPlayable() {
        return getCurrentState() instanceof PlayableGameState;
    }

    /**
     * Retrieves the current state of this game as an instance {@link PlayableGameState}.
     * This will throw an error if the game is not in a playable state.
     * @return The playable state that the game is currently in.
     */
    public @Nonnull PlayableGameState<P, S, R, ?> getCurrentPlayableState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof PlayableGameState)
            return (PlayableGameState<P, S, R, ?>) state;

        throw new IllegalStateException("This game is not in a playable game state");
    }

    /**
     * Determines whether the game is currently in a state that is waiting for a roll from a player.
     * @return Whether the game is currently in a state that is waiting for a roll from a player.
     */
    public boolean isWaitingForRoll() {
        return getCurrentState() instanceof WaitingForRollGameState;
    }

    /**
     * Retrieves the current state of this game as an instance of {@link WaitingForRollGameState}.
     * This will throw an error if the game is not in a state that is waiting for a roll from a player.
     * @return The waiting for roll state that the game is currently in.
     */
    public @Nonnull WaitingForRollGameState<P, S, R> getCurrentWaitingForRollState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WaitingForRollGameState)
            return (WaitingForRollGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not waiting for a roll");
    }

    /**
     * Determines whether the game is currently in a state that is waiting for a move from a player.
     * @return Whether the game is currently in a state that is waiting for a move from a player.
     */
    public boolean isWaitingForMove() {
        return getCurrentState() instanceof WaitingForMoveGameState;
    }

    /**
     * Retrieves the current state of this game as an instance of {@link WaitingForMoveGameState}.
     * This will throw an error if the game is not in a state that is waiting for a move from a player.
     * @return The waiting for move state that the game is currently in.
     */
    public @Nonnull WaitingForMoveGameState<P, S, R> getCurrentWaitingForMoveState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof WaitingForMoveGameState)
            return (WaitingForMoveGameState<P, S, R>) state;

        throw new IllegalStateException("This game is not waiting for a move");
    }

    /**
     * Retrieves the current state of this game as an instance of {@link WinGameState}.
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
     * Retrieves the state of the board in the current state of the game.
     * @return The state of the board in the current state of the game.
     */
    public @Nonnull Board<P> getBoard() {
        return getCurrentState().getBoard();
    }

    /**
     * Retrieves the current state of the light player.
     * @return The current state of the light player.
     */
    public @Nonnull S getLightPlayer() {
        return getCurrentState().getLightPlayer();
    }

    /**
     * Retrieves the current state of the dark player.
     * @return The current state of the dark player.
     */
    public @Nonnull S getDarkPlayer() {
        return getCurrentState().getDarkPlayer();
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
        if (isFinished())
            return getWinner();

        return getCurrentPlayableState().getTurn();
    }

    /**
     * Retrieves the state of the player whose turn it is.
     * @return The state of the player whose turn it is.
     */
    public @Nonnull S getTurnPlayer() {
        return getCurrentPlayableState().getTurnPlayer();
    }

    /**
     * Retrieves the state of the player that is waiting as it is not their turn.
     * @return The state of the player that is waiting as it is not their turn.
     */
    public @Nonnull S getWaitingPlayer() {
        return getCurrentPlayableState().getWaitingPlayer();
    }

    /**
     * Retrieves the player that won the game.
     * @return The player that won the game.
     */
    public @Nonnull PlayerType getWinner() {
        return getCurrentWinState().getWinner();
    }

    /**
     * Retrieves the player that lost the game.
     * @return The player that lost the game.
     */
    public @Nonnull PlayerType getLoser() {
        return getCurrentWinState().getLoser();
    }

    /**
     * Retrieves the state of the winning player.
     * @return The state of the winning player.
     */
    public @Nonnull S getWinningPlayer() {
        return getCurrentWinState().getWinningPlayer();
    }

    /**
     * Retrieves the state of the losing player.
     * @return The state of the losing player.
     */
    public @Nonnull S getLosingPlayer() {
        return getCurrentWinState().getLosingPlayer();
    }

    /**
     * Retrieves the roll that was made that can be used by the
     * current turn player to make a move.
     * @return The roll that was made that can now be used to make a move.
     */
    public @Nonnull R getRoll() {
        return getCurrentWaitingForMoveState().getRoll();
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     * @param settings The settings to initialise the builder with.
     * @return A builder to assist in constructing games with custom settings.
     */
    public static <R extends Roll> @Nonnull GameBuilder<Piece, PlayerState, R>
    builder(GameSettings<R> settings) {
        return new GameBuilder<>(settings, new StandardRuleSetProvider());
    }

    /**
     * Creates a standard game with custom settings.
     * @param settings The settings to use for the game.
     * @return A game with custom settings.
     */
    public static <R extends Roll> @Nonnull Game<Piece, PlayerState, R>
    create(GameSettings<R> settings) {
        return builder(settings).build();
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     * Provides a builder that is set up to produce games using the Finkel
     * rule set by default.
     * @return A builder to assist in constructing games with custom settings.
     */
    public static @Nonnull GameBuilder<Piece, PlayerState, Roll> builder() {
        return builder(GameSettings.FINKEL);
    }

    /**
     * Creates a standard game that follows the rules proposed by Irving Finkel.
     * This uses the simple rules, the standard board shape, Bell's path, safe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    public static @Nonnull Game<Piece, PlayerState, Roll> createFinkel() {
        return builder(GameSettings.FINKEL).build();
    }

    /**
     * Creates a standard game that follows the rules proposed by James Masters.
     * This uses the simple rules, the standard board shape, Bell's path, unsafe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    public static @Nonnull Game<Piece, PlayerState, Roll> createMasters() {
        return builder(GameSettings.MASTERS).build();
    }

    /**
     * Creates a game of Aseb. This uses the simple rules, the Aseb board shape,
     * the Aseb paths, the standard dice, and five starting pieces per player.
     * @return A game of Aseb.
     */
    public static @Nonnull Game<Piece, PlayerState, Roll> createAseb() {
        return builder(GameSettings.ASEB).build();
    }
}
