package company.Pieces;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Board.Squares;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing Bishop piece.
 */
public class Bishop extends MasterAbstractPiece {

    private final transient Logger logger = Logger.getLogger(Bishop.class.getName());

    /**
     * Constructor of Bishop class.
     * @param color color of the Bishop
     */
    public Bishop(PieceColor color){
        super(color, 0);
        this.name = "Bishop";
        this.pieceTag = "B";
        this.pieceType = PieaceType.BISHOP;
    }

    /**
     * Constructor of the Bishop.
     * @param color color of the Bishop
     * @param square    square position of the Bishop
     */
    public Bishop(PieceColor color, Squares square){
        super(color, 0);
        this.name = "Bishop";
        this.pieceTag = "B";
        this.setSquares(square);
        this.pieceType = PieaceType.BISHOP;
    }

    @Override
    public List<BoardLocation> getValidMoves(Board board) {
        List<BoardLocation> moveCandidates = new ArrayList<>();
        Map<BoardLocation, Squares> squareMap = board.getLocationSquaresMap();
        BoardLocation current = this.getSquares().getLocation();
        getMoves(moveCandidates, squareMap, current, 1, 1);
        getMoves(moveCandidates, squareMap, current, 1, -1);
        getMoves(moveCandidates, squareMap, current, -1, -1);
        getMoves(moveCandidates, squareMap, current, -1, 1);
        return moveCandidates;
    }

    /**
     * Method to get moves based on rank and file offset.
     * @param candidates list of move candidates
     * @param squareMap  map of board location and its corresponding squares
     * @param current   current location of the piece
     * @param rankOffset    rank offset
     * @param fileOffset    file offset
     */
    private void getMoves(
            List<BoardLocation> candidates,
            Map<BoardLocation, Squares> squareMap,
            BoardLocation current,
            int rankOffset,
            int fileOffset) {
        try {
            BoardLocation next = LocationUtils.createBoardLocation(current, fileOffset, rankOffset);

            //Iterating over possible move candidates and checking its validity
            while (squareMap.containsKey(next)) {
                if (squareMap.get(next).isTaken()) {
                    if (squareMap.get(next).getCurrentPieceOnSquare().getColor().equals(this.color)) break;
                    candidates.add(next);
                    break;
                }
                candidates.add(next);
                next = LocationUtils.createBoardLocation(next, fileOffset, rankOffset);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }

}
