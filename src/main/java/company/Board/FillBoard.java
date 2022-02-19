package company.Board;

import company.Pieces.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * Class for filling the board at the start of the game.
 */
public final class FillBoard implements Serializable {

    /*
     * Private constructor of the FillBoard class.
     */
    private FillBoard(){}

    /*
     * Method for generating initial pieces' positions.
     * @return  pieces  map of BoardLocations and their pieces
     */
    public static Map<BoardLocation, MasterAbstractPiece> fillBoardWithPieces(){
        Map<BoardLocation, MasterAbstractPiece> pieces = new HashMap<>();


       //Rooks
       pieces.put(new BoardLocation(Files.A, 1), new Rook(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.H, 1), new Rook(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.A, 8), new Rook(PieceColor.BLACK));
       pieces.put(new BoardLocation(Files.H, 8), new Rook(PieceColor.BLACK));

       //Knights
       pieces.put(new BoardLocation(Files.B, 1), new Knight(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.G, 1), new Knight(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.B, 8), new Knight(PieceColor.BLACK));
       pieces.put(new BoardLocation(Files.G, 8), new Knight(PieceColor.BLACK));

       //Bishops
       pieces.put(new BoardLocation(Files.C, 1), new Bishop(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.F, 1), new Bishop(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.C, 8), new Bishop(PieceColor.BLACK));
       pieces.put(new BoardLocation(Files.F, 8), new Bishop(PieceColor.BLACK));

       //Queens
       pieces.put(new BoardLocation(Files.D, 1), new Queen(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.D, 8), new Queen(PieceColor.BLACK));

       //Kings
       pieces.put(new BoardLocation(Files.E, 1), new King(PieceColor.WHITE));
       pieces.put(new BoardLocation(Files.E, 8), new King(PieceColor.BLACK));

       //Pawns
       for(Files file : Files.values()) {
           pieces.put(new BoardLocation(file, 2), new Pawn(PieceColor.WHITE));
           pieces.put(new BoardLocation(file, 7), new Pawn(PieceColor.BLACK));
       }

        return pieces;
    }
}
