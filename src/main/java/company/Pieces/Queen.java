package company.Pieces;

import company.Board.Board;
import company.Board.BoardLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing Queen piece.
 */
public class Queen extends MasterAbstractPiece{
    private MasterAbstractPiece bishop;
    private MasterAbstractPiece rook;

    /**
     * Cosntructor of the Queen piece.
     * @param color color of the Queen
     */
    public Queen(PieceColor color){
        super(color, 0);
        this.name = "Queen";
        this.pieceTag = "Q";
        this.pieceType = PieaceType.QUEEN;
    }

    @Override
    public List<BoardLocation> getValidMoves(Board board) {
        bishop = new Bishop(this.color, this.getSquares());
        rook = new Rook(this.color, this.getSquares());

        List<BoardLocation> moveCandidates = new ArrayList<>();
        moveCandidates.addAll(bishop.getValidMoves(board));
        moveCandidates.addAll(rook.getValidMoves(board));

        return moveCandidates;
    }
}
