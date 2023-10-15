package net.royalur.rules;

import net.royalur.GameMetadata;
import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.simple.SimpleRuleSet;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.state.GameState;
import net.royalur.rules.state.WaitingForMoveGameState;
import net.royalur.rules.state.WaitingForRollGameState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A set of rules that govern the play of a game of the Royal Game of Ur.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public abstract class RuleSet<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> {

    /**
     * The shape of the game board.
     */
    protected final @Nonnull BoardShape boardShape;

    /**
     * The paths that each player must take around the board.
     */
    protected final @Nonnull PathPair paths;

    /**
     * The generator of dice that are used to generate dice rolls.
     */
    protected final @Nonnull DiceFactory<R> diceFactory;

    /**
     * Provides the manipulation of piece values.
     */
    protected final @Nonnull PieceProvider<P> pieceProvider;

    /**
     * Provides the manipulation of player state values.
     */
    protected final @Nonnull PlayerStateProvider<P, S> playerStateProvider;

    /**
     * Instantiates a rule set for the Royal Game of Ur.
     * @param boardShape The shape of the game board.
     * @param paths The paths that the players must take around the board.
     * @param diceFactory The generator of dice that are used to generate dice rolls.
     * @param pieceProvider Provides the manipulation of piece values.
     * @param playerStateProvider Provides the manipulation of player states.
     */
    protected RuleSet(
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull DiceFactory<R> diceFactory,
            @Nonnull PieceProvider<P> pieceProvider,
            @Nonnull PlayerStateProvider<P, S> playerStateProvider
    ) {
        if (!boardShape.isCompatible(paths)) {
            throw new IllegalArgumentException(
                    "The " + paths.getName().getTextName() + " paths are not compatible with the " +
                            boardShape.getName().getTextName() + " board shape"
            );
        }
        this.boardShape = boardShape;
        this.paths = paths;
        this.diceFactory = diceFactory;
        this.pieceProvider = pieceProvider;
        this.playerStateProvider = playerStateProvider;
    }

    /**
     * Gets the shape of the board used in this rule set.
     * @return The shape of the game board.
     */
    public @Nonnull BoardShape getBoardShape() {
        return boardShape;
    }

    /**
     * Gets the paths that the players must take around the board.
     * @return The paths that players must take around the board.
     */
    public @Nonnull PathPair getPaths() {
        return paths;
    }

    /**
     * Gets the generator of dice that are used to generate dice rolls.
     * @return The generator of dice that are used to generate dice rolls.
     */
    public @Nonnull DiceFactory<R> getDiceFactory() {
        return diceFactory;
    }

    /**
     * Gets the provider of piece manipulations.
     * @return The provider of making piece changes.
     */
    public @Nonnull PieceProvider<P> getPieceProvider() {
        return pieceProvider;
    }

    /**
     * Gets the provider of player state manipulations.
     * @return The provider of making player state changes.
     */
    public @Nonnull PlayerStateProvider<P, S> getPlayerStateProvider() {
        return playerStateProvider;
    }

    /**
     * Gets whether rosettes are considered safe squares in this rule set.
     * @return Whether rosettes are considered safe squares in this rule set.
     */
    public abstract boolean areRosettesSafe();

    /**
     * Gets whether landing on rosette tiles grants an additional roll.
     * @return Whether landing on rosette tiles grants an additional roll.
     */
    public abstract boolean doRosettesGrantExtraRolls();

    /**
     * Gets whether capturing a piece grants an additional roll.
     * @return Whether capturing a piece grants an additional roll.
     */
    public abstract boolean doCapturesGrantExtraRolls();

    /**
     * Generates the initial state for a game.
     * @return The initial state for a game.
     */
    public abstract @Nonnull GameState<P, S, R> generateInitialGameState();

    /**
     * Finds all available moves from the given state.
     * @param board The current state of the board.
     * @param player The current state of the player.
     * @param roll The roll that was made. Must be non-zero.
     * @return A list of all the available moves from the given state.
     */
    public abstract @Nonnull List<Move<P>> findAvailableMoves(
            @Nonnull Board<P> board,
            @Nonnull S player,
            @Nonnull R roll
    );

    /**
     * Applies {@code roll} to {@code state} to generate the new state of the
     * game. Multiple game states may be returned to include information game
     * states for maintaining history. However, the latest or highest-index
     * game state will represent the state of the game after the move was made.
     * @param state The current state of the game.
     * @param roll The roll that the player made.
     * @return A list of new game states after the given move was made. The
     *         list may include historical information game states, and will
     *         always include the new state of the game as its last element.
     */
    public abstract @Nonnull List<GameState<P, S, R>> applyRoll(
            @Nonnull WaitingForRollGameState<P, S, R> state,
            @Nonnull R roll
    );

    /**
     * Applies {@code move} to {@code state} to generate the new state of the
     * game. Multiple game states may be returned to include information game
     * states for maintaining history. However, the latest or highest-index
     * game state will represent the state of the game after the move was made.
     * <p>
     * This method does not check that the given move is a valid move
     * from the current game state.
     * @param state The current state of the game.
     * @param move The move that the player chose to make from this position.
     * @return A list of new game states after the given move was made. The
     *         list may include historical information game states, and will
     *         always include the new state of the game as its last element.
     */
    public abstract @Nonnull List<GameState<P, S, R>> applyMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state,
            @Nonnull Move<P> move
    );

    /**
     * Creates a simple rule set that follows the given game settings.
     */
    public static <R extends Roll> @Nonnull SimpleRuleSet<Piece, PlayerState, R>
    createSimple(@Nonnull GameSettings<R> settings) {
        return new SimpleRuleSetProvider().create(settings, new GameMetadata());
    }
}
