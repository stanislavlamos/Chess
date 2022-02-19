package company.Game;

import company.Board.BoardLocation;
import company.Board.Files;
import company.Pieces.King;
import company.Pieces.MasterAbstractPiece;
import company.Pieces.PieceColor;
import company.View.GuiMove;

import javax.swing.*;

/**
 * Method to handle castling during the game of chess.
 */
public class CastlingHandler {
    private GameModel gameModel;
    private String castlingState;

    /**
     * Constructor of the CastlingHandler class.
     * @param gameModel     method storing attributes of chess game and some game logic
     * @param castlingState indicator whether it queenside castling or kingside castling has occurred
     */
    public CastlingHandler(GameModel gameModel, String castlingState){
        this.castlingState = castlingState;
        this.gameModel = gameModel;

        castlingDeterminer();
    }

    /**
     * Method to handle kingside castling castling.
     */
    private void shortCastling(){
        //Displaying window to ask whether the player wants to make kingside castling
        String message = "Would you like to make kingside castling?";
        Integer choice = JOptionPane.showOptionDialog(gameModel.getWindowView(), message, "Castling Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        BoardLocation fromRook;
        BoardLocation toRook;
        BoardLocation fromKing;
        BoardLocation toKing;


        //If the player wants to make kingside castling, add corresponding PGN notation to move history
        if (choice.equals(JOptionPane.OK_OPTION)){
            BoardLocation from = new BoardLocation(Files.A, 1);
            BoardLocation to = new BoardLocation(Files.A, 2);
            PieceColor playerOnTurnColor = gameModel.getPlayerOnTurn().getPlayerPieceColor();
            MasterAbstractPiece kingOnTheMove = new King(playerOnTurnColor);
            String pngNotation = "O-O";
            gameModel.getBoard().addMoveHistory(new Move(gameModel.getPlayerOnTurn(), from, to, kingOnTheMove, pngNotation));

            //Moving pieces based on player's color of pieces
            if (playerOnTurnColor.equals(PieceColor.WHITE)){
                fromRook = new BoardLocation(Files.H, 1);
                toRook = new BoardLocation(Files.F, 1);
                fromKing = new BoardLocation(Files.E, 1);
                toKing = new BoardLocation(Files.G, 1);

            }else {
                fromRook = new BoardLocation(Files.H, 8);
                toRook = new BoardLocation(Files.F, 8);
                fromKing = new BoardLocation(Files.E, 8);
                toKing = new BoardLocation(Files.G, 8);
            }

            //Updating board and redrawing GUI
            gameModel.getGameController().updateBoard(fromRook, toRook);
            gameModel.getGameController().updateBoard(fromKing, toKing);

            GuiMove.guiMove(gameModel.getGameController().getSquarePanel(), gameModel);

            gameModel.changePlayerOnTurn();
        }
    }


    /**
     * Method to handle queenside castling.
     */
    private void longCastling(){
        //Displaying window to ask whether the player wants to make queenside castling
        String message = "Would you like to make queenside castling?";
        Integer choice = JOptionPane.showOptionDialog(gameModel.getWindowView(), message, "Castling Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        BoardLocation fromRook;
        BoardLocation toRook;
        BoardLocation fromKing;
        BoardLocation toKing;


        //If the player wants to make queenside castling, add corresponding PGN notation to move history
        if (choice.equals(JOptionPane.OK_OPTION)){
            BoardLocation from = new BoardLocation(Files.A, 1);
            BoardLocation to = new BoardLocation(Files.A, 2);
            PieceColor playerOnTurnColor = gameModel.getPlayerOnTurn().getPlayerPieceColor();
            MasterAbstractPiece kingOnTheMove = new King(playerOnTurnColor);
            String pngNotation = "O-O-O";
            gameModel.getBoard().addMoveHistory(new Move(gameModel.getPlayerOnTurn(), from, to, kingOnTheMove, pngNotation));

            //Moving pieces based on player's color of pieces
            if (playerOnTurnColor.equals(PieceColor.WHITE)){
                fromRook = new BoardLocation(Files.A, 1);
                toRook = new BoardLocation(Files.D, 1);
                fromKing = new BoardLocation(Files.E, 1);
                toKing = new BoardLocation(Files.C, 1);

            }else {
                fromRook = new BoardLocation(Files.A, 8);
                toRook = new BoardLocation(Files.D, 8);
                fromKing = new BoardLocation(Files.E, 8);
                toKing = new BoardLocation(Files.C, 8);
            }

            //Updating board and redrawing GUI
            gameModel.getGameController().updateBoard(fromRook, toRook);
            gameModel.getGameController().updateBoard(fromKing, toKing);

            GuiMove.guiMove(gameModel.getGameController().getSquarePanel(), gameModel);

            gameModel.changePlayerOnTurn();
        }
    }

    /**
     * determine whether this class should display queenside castling window or kingside castling window.
     */
    private void castlingDeterminer(){
        //Calling queenside castling
        if (this.castlingState.equals("Long")){
            longCastling();
        }

        //Calling kingside window
        else if (this.castlingState.equals("Short")){
            shortCastling();
        }

        //Displaying option window if both castling options are possible
        else {
            String[] buttons = {"queenside", "kingside", "cancel"};
            String message = "Would you like to make queenside or kingside castling?";
            Integer choice = JOptionPane.showOptionDialog(gameModel.getWindowView(), message, "Castling Info", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);

            if (choice == 0){
                longCastling();
            }

            else if (choice == 1){
                shortCastling();
            }
        }
    }
}
