package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.state.*;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * A game of the Royal Game of Ur. Provides methods to allow the playing of games,
 * and methods to support the retrieval of history about the moves that were made.
 */
public class Game {

    /**
     * The set of rules that are being used for this game.
     */
    private final RuleSet rules;

    /**
     * The metadata of this game.
     */
    private final GameMetadata metadata;

    /**
     * The dice to be used to generate dice rolls.
     */
    private final Dice dice;

    /**
     * The states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     */
    private final List<GameState> states;

    /**
     * Instantiates a game of the Royal Game of Ur.
     * @param rules The set of rules that are being used for this game.
     * @param metadata The metadata of this game.
     * @param states The states that have occurred so far in the game.
     */
    public Game(
            RuleSet rules,
            GameMetadata metadata,
            List<GameState> states
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
    public Game(RuleSet rules) {
        this(
                rules,
                GameMetadata.startingNow(),
                List.of(rules.generateInitialGameState())
        );
    }

    /**
     * Instantiates a game of the Royal Game of Ur that is a copy of {@code game}.
     * @param game The rules of the game.
     */
    protected Game(Game game) {
        this(game.rules, game.metadata.copy(), game.states);
        dice.copyFrom(game.dice);
    }

    /**
     * Instantiates a new game of the Royal Game of Ur that is timed.
     * @param rules The rules of the game.
     */
    public static Game createTimed(RuleSet rules) {
        return new Game(rules);
    }

    /**
     * Instantiates a new game of the Royal Game of Ur that is untimed.
     * @param rules The rules of the game.
     */
    public static Game createUntimed(RuleSet rules) {
        return new Game(
                rules,
                new GameMetadata(),
                List.of(rules.generateInitialGameState())
        );
    }

    /**
     * Generates a copy of this game.
     * @return A copy of {@code this}.
     */
    public Game copy() {
        if (!getClass().equals(Game.class)) {
            throw new UnsupportedOperationException(
                    getClass() + " does not support copy"
            );
        }
        return new Game(this);
    }

    /**
     * Adds all states from {@code states} to this game.
     * @param states The states to add to this game.
     */
    public void addStates(Iterable<GameState> states) {
        int seen = 0;
        for (GameState state : states) {
            seen += 1;
            if (state == null)
                throw new IllegalArgumentException("The states list should not contain any null entries");
            if (state instanceof ControlGameState && getLastControlStateOrNull() != null) {
                throw new IllegalArgumentException(
                        "Only a single control game state per game is currently supported"
                );
            }

            addState(state);
        }
        if (seen == 0)
            throw new IllegalArgumentException("There were no states to add");
    }

    /**
     * Adds the state {@code state} to this game.
     * @param state The state to add to this game.
     */
    public void addState(GameState state) {
        states.add(state);
    }

    /**
     * Gets the set of rules that are being used for this game.
     * @return The set of rules that are being used for this game.
     */
    public RuleSet getRules() {
        return rules;
    }

    /**
     * Gets the metadata of this game.
     * @return The metadata of this game.
     */
    public GameMetadata getMetadata() {
        return metadata;
    }

    /**
     * Checks whether this game has a start time and move times are recorded.
     * @return Whether this game has a start time and move times are recorded.
     */
    public boolean isTimed() {
        return metadata.hasStartTime();
    }

    /**
     * Gets the start time of the game in milliseconds since the epoch.
     * If this game is untimed, {@code 0} will be returned instead.
     * @return The start time of the game in milliseconds since the epoch.
     */
    public long getGameStartEpochMs() {
        ZonedDateTime startTime = metadata.getStartTime();
        if (startTime == null)
            return 0;

        return Instant.from(startTime).toEpochMilli();
    }

    /**
     * Gets the number of milliseconds elapsed since the start of the game.
     * @return The number of milliseconds elapsed since the start of the game.
     */
    public long getTimeSinceGameStartMs() {
        long gameStartEpochMs = getGameStartEpochMs();
        if (gameStartEpochMs == 0)
            return 0;

        return System.currentTimeMillis() - gameStartEpochMs;
    }

    /**
     * Gets the dice to are used to make dice rolls.
     * @return The dice to be used to make dice rolls.
     */
    public Dice getDice() {
        return dice;
    }

    /**
     * Gets the states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     * @return The states that have occurred so far in the game.
     */
    public List<GameState> getStates() {
        return List.copyOf(states);
    }

    /**
     * Retrieve the state that the game is currently in.
     * @return The state that the game is currently in.
     */
    public GameState getState() {
        return states.get(states.size() - 1);
    }

    /**
     * Gets the states that represent the actions that have been
     * made so far in the game. The last state in the list represents
     * the last action that was taken in this game.
     * @return The states that represent the actions that have been
     *         made so far in the game.
     */
    public List<ActionGameState> getActionStates() {
        return states.stream()
                .filter(s -> s instanceof ActionGameState)
                .map(s -> (ActionGameState) s)
                .toList();
    }

    /**
     * Gets all moves that were made in the game, as well as
     * the current state of the game. These states are considered
     * landmark states as they contain all the information required
     * to recreate everything that happened in the game so far.
     */
    public List<GameState> getLandmarkStates() {
        return rules.selectLandmarkStates(states);
    }

    /**
     * Determines whether the game is currently in a playable state.
     * @return Whether the game is currently in a playable state.
     */
    public boolean isPlayable() {
        return getState() instanceof PlayableGameState;
    }

    /**
     * Determines whether the game is currently waiting for a roll from a player.
     * @return Whether the game is currently waiting for a roll from a player.
     */
    public boolean isWaitingForRoll() {
        return getState() instanceof WaitingForRollGameState;
    }

    /**
     * Checks whether the game is currently waiting for a move from a player.
     * @return Whether the game is currently waiting for a move from a player.
     */
    public boolean isWaitingForMove() {
        return getState() instanceof WaitingForMoveGameState;
    }

    /**
     * Checks whether the game is currently in a finished state.
     * @return Whether the game is currently in a finished state.
     */
    public boolean isFinished() {
        return getState() instanceof EndGameState;
    }

    /**
     * Checks whether the given player has made any actions in this game.
     * @param player The player to check.
     * @return Whether the given player has made any actions in this game.
     */
    public boolean hasPlayerMadeAnyActions(PlayerType player) {
        for (GameState state : states) {
            if (state instanceof ActionGameState action && action.getTurn() == player)
                return true;
        }
        return false;
    }

    /**
     * Checks whether the given player has made any moves in this game.
     * @param player The player to check.
     * @return Whether the given player has made any moves in this game.
     */
    public boolean hasPlayerMadeAnyMoves(PlayerType player) {
        for (GameState state : states) {
            if (state instanceof MovedGameState move && move.getTurn() == player)
                return true;
        }
        return false;
    }

    /**
     * Gets the current state of this game as a {@link PlayableGameState}.
     * This will throw an error if the game is not in a playable state.
     * @return The playable state that the game is currently in.
     */
    public PlayableGameState getPlayableState() {
        GameState state = getState();
        if (state instanceof PlayableGameState)
            return (PlayableGameState) state;

        throw new IllegalStateException("This game is not in a playable game state");
    }

    /**
     * Gets the current state of this game as an instance of {@link WaitingForRollGameState}.
     * This will throw an error if the game is not waiting for a roll from a player.
     * @return The waiting for roll state that the game is currently in.
     */
    public WaitingForRollGameState getWaitingForRollState() {
        GameState state = getState();
        if (state instanceof WaitingForRollGameState)
            return (WaitingForRollGameState) state;

        throw new IllegalStateException("This game is not waiting for a roll");
    }

    /**
     * Gets the current state of this game as an instance of {@link WaitingForMoveGameState}.
     * This will throw an error if the game is not waiting for a move from a player.
     * @return The waiting for move state that the game is currently in.
     */
    public WaitingForMoveGameState getWaitingForMoveState() {
        GameState state = getState();
        if (state instanceof WaitingForMoveGameState)
            return (WaitingForMoveGameState) state;

        throw new IllegalStateException("This game is not waiting for a move");
    }

    /**
     * Gets the last control state in this game, or {@code null} if there
     * is no control state in this game.
     * @return The last control state in this game, or {@code null}.
     */
    public @Nullable ControlGameState getLastControlStateOrNull() {
        for (int index = states.size() - 1; index >= 0; --index) {
            GameState state = states.get(index);
            if (state instanceof ControlGameState controlState)
                return controlState;
        }
        return null;
    }

    /**
     * Gets the last control state in this game as an instance of {@link ResignedGameState}.
     * is no control state in this game.
     * @return The last control state in this game, or {@code null}.
     */
    public ResignedGameState getResignedState() {
        ControlGameState state = getLastControlStateOrNull();
        if (state instanceof ResignedGameState resignedState)
            return resignedState;

        throw new IllegalStateException("A player did not resign");
    }

    /**
     * Gets the last control state in this game as an instance of {@link AbandonedGameState}.
     * is no control state in this game.
     * @return The last control state in this game, or {@code null}.
     */
    public AbandonedGameState getAbandonedState() {
        ControlGameState state = getLastControlStateOrNull();
        if (state instanceof AbandonedGameState abandonedState)
            return abandonedState;

        throw new IllegalStateException("The game was not abandoned");
    }

    /**
     * Gets the current state of this game as an instance of {@link EndGameState}.
     * This will throw an error if the game has not ended.
     * @return The win state that the game is currently in.
     */
    public EndGameState getEndState() {
        GameState state = getState();
        if (state instanceof EndGameState endState)
            return endState;

        throw new IllegalStateException("This game has not ended");
    }

    /**
     * Rolls the dice, with a known value of {@code roll}, and updates the
     * state of the game accordingly.
     * @param roll The value of the dice that is to be rolled.
     */
    public void rollDice(Roll roll) {
        addStates(rules.applyRoll(
                getWaitingForRollState(),
                getTimeSinceGameStartMs(),
                roll
        ));
    }

    /**
     * Rolls the dice, and updates the state of the game accordingly.
     * @return The value of the dice that were rolled.
     */
    public Roll rollDice() {
        Roll roll = dice.roll();
        rollDice(roll);
        return roll;
    }

    /**
     * Rolls the dice with a known value of {@code value}, and updates
     * the state of the game accordingly.
     * @param value The value of the dice to be rolled.
     * @return The value of the dice that were rolled.
     */
    public Roll rollDice(int value) {
        Roll roll = dice.roll(value);
        rollDice(roll);
        return roll;
    }

    /**
     * Finds all moves that can be made from the current position.
     * @return All moves that can be made from the current position.
     */
    public List<Move> findAvailableMoves() {
        return getWaitingForMoveState().getAvailableMoves();
    }

    /**
     * Finds the move of the piece {@code piece}.
     * @param piece The piece to find the move for.
     */
    public Move findMoveByPiece(Piece piece) {
        for (Move move : findAvailableMoves()) {
            if (move.hasSource() && move.getSourcePiece().equals(piece))
                return move;
        }
        throw new IllegalStateException("The piece cannot be moved, " + piece);
    }

    /**
     * Finds the move of the piece on {@code tile}.
     * @param sourceTile The tile of the piece to find the move for.
     */
    public Move findMoveByTile(Tile sourceTile) {
        PathPair paths = rules.getPaths();
        for (Move move : findAvailableMoves()) {
            if (move.getSource(paths).equals(sourceTile))
                return move;
        }
        throw new IllegalStateException("There is no piece that can be moved on " + sourceTile);
    }

    /**
     * Finds a move that introduces a new piece to the board.
     * @return A move that introduces a new piece to the board.
     */
    public Move findMoveIntroducingPiece() {
        for (Move move : findAvailableMoves()) {
            if (move.isIntroduction())
                return move;
        }
        throw new IllegalStateException("There is no available move that introduces a piece");
    }

    /**
     * Finds a move that scores a piece.
     * @return A move that scores a piece.
     */
    public Move findMoveScoringPiece() {
        for (Move move : findAvailableMoves()) {
            if (move.isScore())
                return move;
        }
        throw new IllegalStateException("There is no available move that scores a piece");
    }

    /**
     * Applies the move {@code move} to update the state of the game.
     * This does not check whether the move is valid.
     * @param move The move to make from the current state of the game.
     */
    public void move(Move move) {
        WaitingForMoveGameState state = getWaitingForMoveState();
        addStates(rules.applyMove(
                state,
                getTimeSinceGameStartMs(),
                move
        ));
    }

    /**
     * Moves the piece {@code piece}, and updates the state of the game.
     * @param piece The piece to be moved.
     */
    public void movePiece(Piece piece) {
        move(findMoveByPiece(piece));
    }

    /**
     * Moves the piece on the given source tile, and updates the state of the game.
     * @param sourceTile The tile where the piece to be moved resides.
     */
    public void movePieceOnTile(Tile sourceTile) {
        move(findMoveByTile(sourceTile));
    }

    /**
     * Marks that {@code player} resigned the game.
     * @param player The player to resign the game.
     */
    public void resign(PlayerType player) {
        if (isFinished())
            throw new IllegalStateException("The game is already finished");

        addStates(rules.applyResign(
                getState(),
                getTimeSinceGameStartMs(),
                player
        ));
    }

    /**
     * Marks that the game was abandoned due to {@code reason}. The person that
     * abandoned the game can be provided using {@code player}, or {@code null}
     * can be passed if a specific player did not abandon the game. For example,
     * if a game had to end due to a venue closing, a player should not be provided.
     * @param reason The reason the game was abandoned.
     * @param player The player that abandoned the game, or {@code null}.
     */
    public void abandon(AbandonReason reason, @Nullable PlayerType player) {
        if (isFinished())
            throw new IllegalStateException("The game is already finished");

        addStates(rules.applyAbandon(
                getState(),
                getTimeSinceGameStartMs(),
                reason,
                player
        ));
    }

    /**
     * Gets whether a player resigned from this game.
     * @return Whether a player resigned from this game.
     */
    public boolean wasResigned() {
        return getLastControlStateOrNull() instanceof ResignedGameState;
    }

    /**
     * Gets the player that resigned from this game.
     * @return The player that resigned from this game.
     */
    public PlayerType getResigningPlayer() {
        return getResignedState().getPlayer();
    }

    /**
     * Gets whether this game was abandoned.
     * @return Whether this game was abandoned.
     */
    public boolean wasAbandoned() {
        return getLastControlStateOrNull() instanceof AbandonedGameState;
    }

    /**
     * Gets the reason this game was abandoned.
     * @return The reason this game was abandoned.
     */
    public AbandonReason getAbandonReason() {
        return getAbandonedState().getReason();
    }

    /**
     * Gets whether a specific player abandoned the game.
     * @return Whether a specific player abandoned the game.
     */
    public boolean wasAbandonedByPlayer() {
        return getAbandonedState().hasPlayer();
    }

    /**
     * Gets the player that abandoned the game.
     * @return The player that abandoned the game.
     */
    public PlayerType getAbandoningPlayer() {
        return getAbandonedState().getPlayer();
    }

    /**
     * Gets the current state of the board.
     * @return The current state of the board.
     */
    public Board getBoard() {
        return getState().getBoard();
    }

    /**
     * Gets the current state of the light player.
     * @return The current state of the light player.
     */
    public PlayerState getLightPlayer() {
        return getState().getLightPlayer();
    }

    /**
     * Gets the current state of the dark player.
     * @return The current state of the dark player.
     */
    public PlayerState getDarkPlayer() {
        return getState().getDarkPlayer();
    }

    /**
     * Gets the current state of the player {@code player}.
     * @param player The player to get the state of.
     * @return The state of the player {@code player}.
     */
    public PlayerState getPlayer(PlayerType player) {
        return getState().getPlayerState(player);
    }

    /**
     * Gets the player who can make the next interaction with the game.
     * @return The player who can make the next interaction with the game.
     */
    public PlayerType getTurn() {
        return getPlayableState().getTurn();
    }

    /**
     * Gets the state of the player whose turn it is.
     * @return The state of the player whose turn it is.
     */
    public PlayerState getTurnPlayer() {
        return getPlayableState().getTurnPlayer();
    }

    /**
     * Gets the player that is waiting whilst the other player makes the
     * next interaction with the game.
     * @return The player who is waiting for the other player to interact
     *         with the game.
     */
    public PlayerType getWaiting() {
        return getPlayableState().getWaiting();
    }

    /**
     * Gets the state of the player that is waiting as it is not their turn.
     * @return The state of the player that is waiting as it is not their turn.
     */
    public PlayerState getWaitingPlayer() {
        return getPlayableState().getWaitingPlayer();
    }

    /**
     * Gets whether this game has a winner.
     * @return Whether this game has a winner.
     */
    public boolean hasWinner() {
        return isFinished() && getEndState().hasWinner();
    }

    /**
     * Gets the player that won the game.
     * @return The player that won the game.
     */
    public PlayerType getWinner() {
        return getEndState().getWinner();
    }

    /**
     * Gets whether this game has a winner.
     * @return Whether this game has a winner.
     */
    public boolean hasLoser() {
        return isFinished() && getEndState().hasLoser();
    }

    /**
     * Gets the player that lost the game.
     * @return The player that lost the game.
     */
    public PlayerType getLoser() {
        return getEndState().getLoser();
    }

    /**
     * Gets the state of the winning player.
     * @return The state of the winning player.
     */
    public PlayerState getWinningPlayer() {
        return getEndState().getWinningPlayer();
    }

    /**
     * Gets the state of the losing player.
     * @return The state of the losing player.
     */
    public PlayerState getLosingPlayer() {
        return getEndState().getLosingPlayer();
    }

    /**
     * Gets the player who can make the next interaction with the game,
     * or the winner of the game if it is finished.
     * @return The player who can make the next interaction with the game,
     *         or the winner of the game if it is finished.
     */
    public PlayerType getTurnOrWinner() {
        if (isPlayable())
            return getTurn();

        if (isFinished())
            return getWinner();

        throw new IllegalStateException("The game is not playable or finished");
    }

    /**
     * Gets the roll that was made that can be used by the
     * current turn player to make a move.
     * @return The roll that was made that can now be used to make a move.
     */
    public Roll getRoll() {
        return getWaitingForMoveState().getRoll();
    }

    /**
     * Creates a builder to assist in constructing games with custom settings.
     */
    public static GameBuilder builder() {
        return new GameBuilder(GameSettings.FINKEL, new SimpleRuleSetProvider());
    }

    /**
     * Creates a simple game with custom settings.
     * @param settings The settings to use for the game.
     * @return A game with custom settings.
     */
    public static Game create(GameSettings settings) {
        return builder().replaceSettings(settings).build();
    }

    /**
     * Creates a game that follows the rules proposed by Irving Finkel.
     * This uses the simple rules, the standard board shape, Bell's path, safe
     * rosette tiles, the standard dice, and seven starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    public static Game createFinkel() {
        return create(GameSettings.FINKEL);
    }

    /**
     * Creates a game that follows the rules proposed by James Masters.
     * This uses the simple rules, the standard board shape, Bell's path, unsafe
     * rosette tiles, three binary dice where 0 allows moving 4 tiles, and seven
     * starting pieces per player.
     * @return A game that follows Irving Finkel's proposed simple rules.
     */
    public static Game createMasters() {
        return create(GameSettings.MASTERS);
    }

    /**
     * Creates a game of Aseb. This uses the simple rules, the Aseb board shape,
     * the Aseb paths, the standard dice, and five starting pieces per player.
     * @return A game of Aseb.
     */
    public static Game createAseb() {
        return create(GameSettings.ASEB);
    }
}
