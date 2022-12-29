package net.royalur;

import net.royalur.agent.Agent;
import net.royalur.model.*;
import net.royalur.model.state.*;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.SimpleGame;
import net.royalur.rules.simple.SimplePiece;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A game of the Royal Game of Ur. Provides methods to allow the playing of games,
 * and methods to support the retrieval of history about the moves that were made
 * in games that have been played.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public class Game<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The standard format to use for Date metadata values.
     */
    public static final @Nonnull String DATE_PATTERN = "yyyy.MM.dd";

    /**
     * The standard format to use for Time metadata values.
     */
    public static final @Nonnull String TIME_PATTERN = "HH:mm:ss";

    /**
     * The standard format to use for TimeZone metadata values.
     */
    public static final @Nonnull String TIMEZONE_PATTERN = "OOOO";

    /**
     * The set of rules that are being used for this game.
     */
    public final @Nonnull RuleSet<P, S, R> rules;

    /**
     * Arbitrary metadata about this game.
     */
    private final @Nonnull Map<String, String> metadata;

    /**
     * The states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     */
    private final @Nonnull List<GameState<P, S, R>> states;

    /**
     * Instantiates a game of the Royal Game of Ur.
     * @param rules The set of rules that are being used for this game.
     * @param states The states that have occurred so far in the game.
     */
    public Game(@Nonnull RuleSet<P, S, R> rules, @Nonnull List<GameState<P, S, R>> states) {
        if (states.isEmpty())
            throw new IllegalArgumentException("Games must have at least one state to play from");

        this.rules = rules;
        this.metadata = new LinkedHashMap<>();
        this.states = new ArrayList<>();

        addStates(states);

        GameState<P, S, R> state = getCurrentState();
        metadata.put("Rules", rules.getDescriptor());
//        metadata.put("Light", state.lightPlayer.name);
//        metadata.put("Dark", state.darkPlayer.name);

        ZonedDateTime now = ZonedDateTime.now();
        metadata.put("Date", DateTimeFormatter.ofPattern(DATE_PATTERN).format(now));
        metadata.put("Time", DateTimeFormatter.ofPattern(TIME_PATTERN).format(now));
        metadata.put("TimeZone", DateTimeFormatter.ofPattern(TIMEZONE_PATTERN).format(now));
    }

    /**
     * Instantiates a game of the Royal Game of Ur that has not yet had any moves played.
     * @param rules The rules of the game.
     */
    public Game(@Nonnull RuleSet<P, S, R> rules) {
        this(
                rules,
                List.of(new WaitingForRollGameState<>(
                        rules.generateEmptyBoard(),
                        rules.generateNewPlayerState(Player.LIGHT),
                        rules.generateNewPlayerState(Player.DARK),
                        Player.LIGHT
                ))
        );
    }

    /**
     * Sets the value of the metadata associated with the key {@code key} to {@code value}.
     * @param key   The key of the metadata item to be updated.
     * @param value The new value to be associated with the given metadata key.
     */
    public void putMetadata(@Nonnull String key, @Nonnull String value) {
        metadata.put(key, value);
    }

    /**
     * Removes any metadata associated with the key {@code key}.
     * @param key   The key of the metadata item to be removed.
     */
    public void removeMetadata(@Nonnull String key) {
        metadata.remove(key);
    }

    /**
     * Retrieves the metadata associated with the key {@code key}.
     * @param key The key of the metadata item to be retrieved.
     * @return The metadata associated with the key {@code key}.
     * @throws IllegalStateException If there is no metadata associated with the key {@code key}.
     */
    public @Nonnull String getMetadata(@Nonnull String key) {
        String value = metadata.get(key);
        if (value != null)
            return value;

        throw new IllegalStateException(
                "This game does not contain any metadata associated with the key '" + key + "'"
        );
    }

    /**
     * Retrieves all metadata about this game.
     * @return The metadata about this game.
     */
    public @Nonnull Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
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
     * Retrieves the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    public @Nonnull List<ActionGameState<P, S, R>> getActionStates() {
        List<ActionGameState<P, S, R>> actionStates = new ArrayList<>();
        for (GameState<P, S, R> state : states) {
            if (state instanceof ActionGameState) {
                actionStates.add((ActionGameState<P, S, R>) state);
            }
        }
        return Collections.unmodifiableList(actionStates);
    }

    /**
     * Retrieves the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    public @Nonnull List<GameState<P, S, R>> getLandmarkStates() {
        List<GameState<P, S, R>> landmarkStates = new ArrayList<>();
        for (int index = 0; index < states.size(); ++index) {
            GameState<P, S, R> state = states.get(index);

            // Always include the last state.
            if (index == states.size() - 1) {
                landmarkStates.add(state);
                continue;
            }

            // There will only be one move game state per turn.
            if (state instanceof MovedGameState) {
                landmarkStates.add(state);
            }
        }
        return Collections.unmodifiableList(landmarkStates);
    }

    /**
     * Adds all states from {@code states} to this game.
     * @param states The states to add to this game.
     */
    private void addStates(@Nonnull Iterable<GameState<P, S, R>> states) {
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
    private void addState(@Nonnull GameState<P, S, R> state) {
        // Actually add the state to this game!
        states.add(state);
    }

    /**
     * Retrieve the type of state that the game is currently in.
     * @return The type of state that the game is currently in.
     */
    public @Nonnull GameStateType getCurrentStateType() {
        return getCurrentState().type;
    }

    /**
     * Retrieve the state that the game is currently in.
     * @return The state that the game is currently in.
     */
    public @Nonnull GameState<P, S, R> getCurrentState() {
        return states.get(states.size() - 1);
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
    public @Nonnull PlayableGameState<P, S, R> getCurrentPlayableState() {
        GameState<P, S, R> state = getCurrentState();
        if (state instanceof PlayableGameState)
            return (PlayableGameState<P, S, R>) state;

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
     * Determines whether the game has finished.
     * @return Whether the game has finished.
     */
    public boolean hasEnded() {
        return getCurrentState() instanceof WinGameState;
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
        return getCurrentState().board;
    }

    /**
     * Retrieves the current state of the light player.
     * @return The current state of the light player.
     */
    public @Nonnull S getLightPlayer() {
        return getCurrentState().lightPlayer;
    }

    /**
     * Retrieves the current state of the dark player.
     * @return The current state of the dark player.
     */
    public @Nonnull S getDarkPlayer() {
        return getCurrentState().darkPlayer;
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
     * Retrieves the state of the winning player.
     * @return The state of the winning player.
     */
    public @Nonnull S getWinner() {
        return getCurrentWinState().getWinner();
    }

    /**
     * Retrieves the state of the losing player.
     * @return The state of the losing player.
     */
    public @Nonnull S getLoser() {
        return getCurrentWinState().getLoser();
    }

    /**
     * Retrieves the roll that was made that can be used by the
     * current turn player to make a move.
     * @return The roll that was made that can now be used to make a move.
     */
    public @Nonnull R getRoll() {
        return getCurrentWaitingForMoveState().roll;
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
        R roll = rules.rollDice();
        rollDice(roll);
        return roll;
    }

    /**
     * Rolls the dice with a known value of {@code value}, and updates the state of the game accordingly.
     * @param value The value of the dice to be rolled.
     * @return The value of the dice that were rolled.
     */
    public @Nonnull R rollDice(int value) {
        R roll = rules.rollDice(value);
        rollDice(roll);
        return roll;
    }

    /**
     * Finds all available moves that can be made from the current state of the game.
     * @return All available moves that can be made from the current state of the game.
     */
    public @Nonnull List<Move<P>> findAvailableMoves() {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        return rules.findAvailableMoves(state.board, state.getTurnPlayer(), state.roll);
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
     * Moves the piece on tile {@code tile}, and updates the state of the game.
     * @param tile The tile where the piece to be moved resides.
     */
    public void makeMove(@Nonnull Tile tile) {
        PlayableGameState<P, S, R> state = getCurrentPlayableState();
        if (!state.board.contains(tile)) {
            if (tile.equals(rules.paths.get(state.turn).startTile)) {
                makeMoveIntroducingPiece();
                return;
            }
            throw new IllegalStateException("The tile does not exist on the board, " + tile);
        }

        P piece = getCurrentState().board.get(tile);
        if (piece == null)
            throw new IllegalStateException("There is no piece on tile " + tile);

        makeMove(piece);
    }

    /**
     * Completes this game using the two agents to play its moves.
     * @param light The agent to play as the light player.
     * @param dark The agent to play as the dark player.
     * @return The number of actions that were made by both agents combined. Includes rolls of the dice and moves.
     */
    public int playAutonomously(@Nonnull Agent<P, S, R> light, @Nonnull Agent<P, S, R> dark) {
        int actions = 0;
        while (!isFinished()) {
            if (!isPlayable()) {
                throw new IllegalStateException(
                        "Encountered an unplayable state that is not the end of the game: " +
                                getCurrentState().getClass().getSimpleName()
                );
            }

            actions += 1;
            S turnPlayer = getTurnPlayer();
            switch (turnPlayer.player) {
                case LIGHT:
                    light.playTurn(this, Player.LIGHT);
                    break;
                case DARK:
                    dark.playTurn(this, Player.DARK);
                    break;
                default:
                    throw new IllegalStateException("Unknown player " + turnPlayer.player);
            }
        }
        return actions;
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     * @return A builder to assist in constructing games with custom settings.
     */
    public static @Nonnull GameBuilder builder() {
        return new GameBuilder();
    }

    /**
     * Creates a standard game that follows the default rules on RoyalUr.net. Therefore,
     * this uses the simple rules, the standard board shape, Bell's path, the standard dice,
     * and seven starting pieces per player.
     * @return A standard game.
     */
    public static @Nonnull SimpleGame createStandard() {
        return builder().standard().build();
    }

    /**
     * Creates a game of Aseb. This uses the simple rules, the Aseb board shape,
     * the Aseb paths, the standard dice, and five starting pieces per player.
     * @return A game of Aseb.
     */
    public static @Nonnull SimpleGame createAseb() {
        return builder().aseb().build();
    }
}
