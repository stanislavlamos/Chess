package com.company.Tests;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Board.Squares;
import company.Game.GameController;
import company.Game.GameModel;
import company.Game.GameStates;
import company.Pieces.MasterAbstractPiece;
import company.Pieces.PieaceType;
import company.Pieces.PieceColor;
import company.Player.AbstractPlayer;
import company.Player.ComputerPlayer;
import company.Player.PlayerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    private Board board = new Board();
    private AbstractPlayer player1 = new ComputerPlayer("Computer player", PieceColor.BLACK, PlayerType.COMPUTERPLAYER);
    private AbstractPlayer player2 = new ComputerPlayer("Human player", PieceColor.WHITE, PlayerType.HUMANPLAYER);
    private GameModel gameModel = new GameModel(player1, player2, board);
    private GameController gameController = new GameController(gameModel);

    @Test
    void startGame() {
        this.gameController.startGame();
        AbstractPlayer []playerArray = {player1, player2};

        AbstractPlayer playerOnTurn = player1.getPlayerPieceColor().equals(PieceColor.WHITE)
                ? player1 : player2;

        assertEquals(playerOnTurn, gameModel.getPlayerOnTurn());
        assertEquals(GameStates.ACTIVE, gameModel.getGameState());
        assertArrayEquals(playerArray, gameModel.getPlayerArr());
    }


    @Test
    void updateBoard() {
        //Moving white Pawns one square forward
        for (int i = 0; i < 8; i++){
            gameModel.getGameController().updateBoard(gameModel.getBoard().getBoardArray()[i][1].getLocation()
                    , gameModel.getBoard().getBoardArray()[i][2].getLocation());
        }

        for (int i = 0; i < 8; i++){
            assertEquals(PieaceType.PAWN, gameModel.getBoard().getBoardArray()[i][2].getCurrentPieceOnSquare().getPieceType());
        }
    }

    @Test
    void stopChessTimerForPlayerOnturn() {

        gameModel.setPlayerOnTurn(player1);
        for (int i = 0; i < 2; i++){
            gameModel.getGameController().stopChessTimerForPlayerOnturn();

            if (gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.COMPUTERPLAYER)){
                continue;
            }

            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                assertFalse(gameModel.getWhiteTimer().isActive());
            }else {
                assertFalse(gameModel.getBlackTimer().isActive());
            }

            gameModel.changePlayerOnTurn();
        }
    }

    @Test
    void resumeChessTimerForPlayerOnTurn() {

        gameModel.setPlayerOnTurn(player1);
        for (int i = 0; i < 2; i++){
            gameModel.getGameController().resumeChessTimerForPlayerOnTurn();

            if (gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.COMPUTERPLAYER)){
                continue;
            }

            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                assertTrue(gameModel.getWhiteTimer().isActive());
            }else {
                assertTrue(gameModel.getBlackTimer().isActive());
            }

            gameModel.changePlayerOnTurn();
        }
    }
}