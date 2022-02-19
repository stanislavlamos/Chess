package company.View;

import company.Board.BoardLocation;
import company.Game.GameModel;
import company.Pieces.*;

import javax.swing.*;

/**
 * Class which represents piece editor
 */
public class PieceEditor {
    GameModel gameModel;
    BoardTile curTile;
    SquarePanel squarePanel;

    /**
     * Constructor of the PieceEditor class.
     */
    public PieceEditor(){}

    /**
     * Method to show piece editor window.
     * @param gameModel instance of the gameModel
     * @param curTile   current tile on chess board
     * @param squarePanel   instance of the squarePanel
     */
    public void showDialog(GameModel gameModel, BoardTile curTile, SquarePanel squarePanel){
        this.gameModel = gameModel;
        this.curTile = curTile;
        this.squarePanel = squarePanel;
        BoardLocation tileLocation = curTile.getTileLocation();


        //List of buttons
        String[] choices = { "Black Pawn", "Black King", "Black Queen", "Black Knight", "Black Bishop", "Black Rook",
                "White Pawn", "White King", "White Queen", "White Knight", "White Bishop", "White Rook"};

        String input = (String) JOptionPane.showInputDialog(gameModel.getWindowView(), "Choose now...",
                "Choose a piece you want to place on selected tile", JOptionPane.QUESTION_MESSAGE, null, choices, null);

        //Handling user selection
        if (input != null){
            String pieceColorString = input.substring(0, 5);
            PieceColor pieceColor = pieceColorString.equals("Black") ? PieceColor.BLACK : PieceColor.WHITE;

            String pieceString = input.substring(6);
            MasterAbstractPiece pieceToPlace;

            System.out.println(pieceColorString);

            //Selection cases which may occur
            switch (pieceString){
                case "Pawn":
                    pieceToPlace = new Pawn(pieceColor);
                    break;

                case "King":
                    pieceToPlace = new King(pieceColor);
                    break;

                case "Bishop":
                    pieceToPlace = new Bishop(pieceColor);
                    break;

                case "Rook":
                    pieceToPlace = new Rook(pieceColor);
                    break;

                case "Queen":
                    pieceToPlace = new Queen(pieceColor);
                    break;

                default:
                    pieceToPlace = new Knight(pieceColor);
                    break;
            }

            //Updating and redrawing the board
            gameModel.getBoard().getLocationSquaresMap().get(tileLocation).setOccupation(true);
            gameModel.getBoard().getLocationSquaresMap().get(tileLocation).setCurrentPieceOnSquare(pieceToPlace);
            pieceToPlace.setSquares(gameModel.getBoard().getLocationSquaresMap().get(tileLocation));
            GuiMove.guiMove(this.squarePanel, this.gameModel);
        }

    }
}
