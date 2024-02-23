package net.royalur.notation;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import java.util.List;

/**
 * Produces game states from scratch.
 */
public class FullStateSource extends StateSource {

    private final Board board;
    private final PlayerState lightPlayer;
    private final PlayerState darkPlayer;

    public FullStateSource(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer
    ) {
        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    private PlayerState getPlayer(PlayerType turn) {
        return switch (turn) {
            case LIGHT -> lightPlayer;
            case DARK -> darkPlayer;
        };
    }

    @Override
    public RolledGameState createRolledState(
            RuleSet rules,
            PlayerType turn,
            Roll roll
    ) {
        List<Move> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new RolledGameState(
                board, lightPlayer, darkPlayer,
                turn, roll, availableMoves
        );
    }

    @Override
    public MovedGameState createMovedState(
            RuleSet rules,
            PlayerType turn,
            Roll roll,
            Move move
    ) {
        return new MovedGameState(
                board, lightPlayer, darkPlayer,
                turn, roll, move
        );
    }

    @Override
    public WaitingForRollGameState createWaitingForRollState(
            RuleSet rules,
            PlayerType turn
    ) {
        return new WaitingForRollGameState(
                board, lightPlayer, darkPlayer, turn
        );
    }

    @Override
    public WaitingForMoveGameState createWaitingForMoveState(
            RuleSet rules,
            PlayerType turn,
            Roll roll
    ) {
        List<Move> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new WaitingForMoveGameState(
                board, lightPlayer, darkPlayer,
                turn, roll, availableMoves
        );
    }

    @Override
    public WinGameState createWinState(
            RuleSet rules,
            PlayerType winner
    ) {
        return new WinGameState(
                board, lightPlayer, darkPlayer, winner
        );
    }
}
