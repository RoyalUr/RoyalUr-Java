package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.GameState;
import net.royalur.rules.state.PlayableGameState;
import net.royalur.rules.state.WaitingForMoveGameState;
import net.royalur.rules.state.WaitingForRollGameState;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.random.RandomGenerator;

/**
 * A game of the Royal Game of Ur. Provides methods to allow the playing of games,
 * and methods to support the retrieval of history about the moves that were made
 * in games that have been played.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public class BasicGame<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> implements Game<P, S, R> {

    /**
     * The standard format to use for Date metadata values.
     */
    private static final @Nonnull String DATE_PATTERN = "yyyy.MM.dd";

    /**
     * The standard format to use for Time metadata values.
     */
    private static final @Nonnull String TIME_PATTERN = "HH:mm:ss";

    /**
     * The standard format to use for TimeZone metadata values.
     */
    private static final @Nonnull String TIMEZONE_PATTERN = "OOOO";

    /**
     * The source of randomness to use for generating dice rolls.
     */
    private final @Nonnull RandomGenerator random;

    /**
     * The set of rules that are being used for this game.
     */
    private final @Nonnull RuleSet<P, S, R> rules;

    /**
     * The dice to be used to generate dice rolls.
     */
    private final @Nonnull Dice<R> dice;

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
     * @param random The source of randomness to use to generate dice rolls.
     * @param rules The set of rules that are being used for this game.
     * @param states The states that have occurred so far in the game.
     */
    public BasicGame(
            @Nonnull RandomGenerator random,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull List<GameState<P, S, R>> states
    ) {
        if (states.isEmpty())
            throw new IllegalArgumentException("Games must have at least one state to play from");

        this.random = random;
        this.rules = rules;
        this.dice = rules.getDiceFactory().createDice(random);
        this.metadata = new LinkedHashMap<>();
        this.states = new ArrayList<>();

        addStates(states);

        ZonedDateTime now = ZonedDateTime.now();
        metadata.put("Date", DateTimeFormatter.ofPattern(DATE_PATTERN).format(now));
        metadata.put("Time", DateTimeFormatter.ofPattern(TIME_PATTERN).format(now));
        metadata.put("TimeZone", DateTimeFormatter.ofPattern(TIMEZONE_PATTERN).format(now));
    }

    /**
     * Instantiates a game of the Royal Game of Ur that has not yet had any moves played.
     * @param random The source of randomness to use to generate dice rolls.
     * @param rules The rules of the game.
     */
    public BasicGame(@Nonnull RandomGenerator random, @Nonnull RuleSet<P, S, R> rules) {
        this(random, rules, List.of(rules.generateInitialGameState()));
    }

    /**
     * Instantiates a game of the Royal Game of Ur that is a copy of {@code game}.
     * @param game The rules of the game.
     */
    private BasicGame(@Nonnull BasicGame<P, S, R> game) {
        this(game.random, game.rules, game.states);
        metadata.clear();
        metadata.putAll(game.metadata);
    }

    @Override
    public @Nonnull BasicGame<P, S, R> copy() {
        return new BasicGame<>(this);
    }

    @Override
    public @Nonnull RuleSet<P, S, R> getRules() {
        return rules;
    }

    @Override
    public @Nonnull Dice<R> getDice() {
        return dice;
    }

    @Override
    public void putMetadata(@Nonnull String key, @Nonnull String value) {
        metadata.put(key, value);
    }

    @Override
    public void removeMetadata(@Nonnull String key) {
        metadata.remove(key);
    }

    @Override
    public @Nonnull String getMetadata(@Nonnull String key) {
        String value = metadata.get(key);
        if (value != null)
            return value;

        throw new IllegalStateException(
                "This game does not contain any metadata associated with the key '" + key + "'"
        );
    }

    @Override
    public @Nonnull Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public @Nonnull List<GameState<P, S, R>> getStates() {
        return Collections.unmodifiableList(states);
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

    @Override
    public @Nonnull GameState<P, S, R> getCurrentState() {
        return states.get(states.size() - 1);
    }

    @Override
    public void rollDice(@Nonnull R roll) {
        WaitingForRollGameState<P, S, R> state = getCurrentWaitingForRollState();
        addStates(rules.applyRoll(state, roll));
    }

    @Override
    public @Nonnull R rollDice() {
        R roll = dice.roll();
        rollDice(roll);
        return roll;
    }

    @Override
    public @Nonnull R rollDice(int value) {
        R roll = dice.generateRoll(value);
        rollDice(roll);
        return roll;
    }

    @Override
    public @Nonnull List<Move<P>> findAvailableMoves() {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        return rules.findAvailableMoves(state.getBoard(), state.getTurnPlayer(), state.getRoll());
    }

    @Override
    public void makeMove(@Nonnull Move<P> move) {
        WaitingForMoveGameState<P, S, R> state = getCurrentWaitingForMoveState();
        addStates(rules.applyMove(state, move));
    }

    @Override
    public void makeMove(@Nonnull P piece) {
        for (Move<P> move : findAvailableMoves()) {
            if (!move.getSourcePiece().equals(piece))
                continue;

            makeMove(move);
            return;
        }
        throw new IllegalStateException("The piece cannot be moved, " + piece);
    }

    @Override
    public void makeMoveIntroducingPiece() {
        for (Move<P> move : findAvailableMoves()) {
            if (!move.isIntroducingPiece())
                continue;

            makeMove(move);
            return;
        }
        throw new IllegalStateException("There is no available move to introduce a piece to the board");
    }

    @Override
    public void makeMove(@Nonnull Tile tile) {
        PlayableGameState<P, S, R, ?> state = getCurrentPlayableState();
        if (!state.getBoard().contains(tile)) {
            if (tile.equals(rules.getPaths().getStart(state.getTurn()))) {
                makeMoveIntroducingPiece();
                return;
            }
            throw new IllegalStateException("The tile does not exist on the board, " + tile);
        }

        P piece = getCurrentState().getBoard().get(tile);
        if (piece == null)
            throw new IllegalStateException("There is no piece on tile " + tile);

        makeMove(piece);
    }
}
