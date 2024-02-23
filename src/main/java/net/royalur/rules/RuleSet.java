package net.royalur.rules;

import net.royalur.model.GameMetadata;
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

import java.util.List;

/**
 * A set of rules that govern the play of a game of the Royal Game of Ur.
 */
public abstract class RuleSet {

    /**
     * The shape of the game board.
     */
    protected final BoardShape boardShape;

    /**
     * The paths that each player must take around the board.
     */
    protected final PathPair paths;

    /**
     * The generator of dice that are used to generate dice rolls.
     */
    protected final DiceFactory diceFactory;

    /**
     * Provides the manipulation of piece values.
     */
    protected final PieceProvider pieceProvider;

    /**
     * Provides the manipulation of player state values.
     */
    protected final PlayerStateProvider playerStateProvider;

    /**
     * Instantiates a rule set for the Royal Game of Ur.
     * @param boardShape The shape of the game board.
     * @param paths The paths that the players must take around the board.
     * @param diceFactory The generator of dice that are used to generate dice rolls.
     * @param pieceProvider Provides the manipulation of piece values.
     * @param playerStateProvider Provides the manipulation of player states.
     */
    protected RuleSet(
            BoardShape boardShape,
            PathPair paths,
            DiceFactory diceFactory,
            PieceProvider pieceProvider,
            PlayerStateProvider playerStateProvider
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
     * Get the settings used for this rule set.
     * @return The settings used for this rule set.
     */
    public GameSettings getSettings() {
        return new GameSettings(
                boardShape,
                paths,
                diceFactory,
                playerStateProvider.getStartingPieceCount(),
                areRosettesSafe(),
                doRosettesGrantExtraRolls(),
                doCapturesGrantExtraRolls()
        );
    }

    /**
     * Gets the shape of the board used in this rule set.
     * @return The shape of the game board.
     */
    public BoardShape getBoardShape() {
        return boardShape;
    }

    /**
     * Gets the paths that the players must take around the board.
     * @return The paths that players must take around the board.
     */
    public PathPair getPaths() {
        return paths;
    }

    /**
     * Gets the generator of dice that are used to generate dice rolls.
     * @return The generator of dice that are used to generate dice rolls.
     */
    public DiceFactory getDiceFactory() {
        return diceFactory;
    }

    /**
     * Gets the provider of piece manipulations.
     * @return The provider of making piece changes.
     */
    public PieceProvider getPieceProvider() {
        return pieceProvider;
    }

    /**
     * Gets the provider of player state manipulations.
     * @return The provider of making player state changes.
     */
    public PlayerStateProvider getPlayerStateProvider() {
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
    public abstract GameState generateInitialGameState();

    /**
     * Finds all available moves from the given state.
     * @param board The current state of the board.
     * @param player The current state of the player.
     * @param roll The roll that was made. Must be non-zero.
     * @return A list of all the available moves from the given state.
     */
    public abstract List<Move> findAvailableMoves(
            Board board,
            PlayerState player,
            Roll roll
    );

    /**
     * Applies {@code roll} to {@code state} to generate the new state of the
     * game. Multiple game states may be returned to include information game
     * states for maintaining history. However, the latest or highest-index
     * game state will represent the state of the game after the roll was made.
     * @param state The current state of the game.
     * @param roll The roll that the player made.
     * @return A list of new game states after the given move was made. The
     *         list may include historical information game states, and will
     *         always include the new state of the game as its last element.
     */
    public abstract List<GameState> applyRoll(
            WaitingForRollGameState state,
            Roll roll
    );

    /**
     * Applies {@code move} to {@code state} to generate the new state of the
     * game. Multiple game states may be returned to include information game
     * states for maintaining history. However, the latest or highest-index
     * game state will represent the state of the game after the move was made.
     * <p>
     * This method does not check that the given move is valid.
     * @param state The current state of the game.
     * @param move The move that the player chose to make from this position.
     * @return A list of new game states after the given move was made. The
     *         list may include historical information game states, and will
     *         always include the new state of the game as its last element.
     */
    public abstract List<GameState> applyMove(
            WaitingForMoveGameState state,
            Move move
    );

    /**
     * Selects only the states that are required to reproduce
     * exactly what happened in a game using this rule set.
     * This is used to reduce the amount of information saved
     * during serialisation.
     * @param states All the states in a game.
     */
    public abstract List<GameState> selectLandmarkStates(
            List<GameState> states
    );

    /**
     * Creates a simple rule set that follows the given game settings.
     */
    public static SimpleRuleSet createSimple(GameSettings settings) {
        return new SimpleRuleSetProvider().create(settings, new GameMetadata());
    }
}
