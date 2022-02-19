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
 * Class representing the Knight piece.
 */
public class Knight extends MasterAbstractPiece{
    private static final transient Logger logger = Logger.getLogger(King.class.getName());

    /**
     * Constructor of the Knight class.
     * @param color color of the Knight
     */
    public Knight(PieceColor color){
        super(color, 0);
        this.name = "Knight";
        this.pieceTag = "N";
        this.pieceType = PieaceType.KNIGHT;
    }

    @Override
    public List<BoardLocation> getValidMoves(Board board) {
        List<BoardLocation> moveCandidates = new ArrayList<>();
        Map<BoardLocation, Squares> squareMap = board.getLocationSquaresMap();
        BoardLocation current = this.getSquares().getLocation();
        getMoves(moveCandidates, squareMap, current, 2, 1);
        getMoves(moveCandidates, squareMap, current, 2, -1);
        getMoves(moveCandidates, squareMap, current, -2, 1);
        getMoves(moveCandidates, squareMap, current, -2, -1);
        getMoves(moveCandidates, squareMap, current, 1, 2);
        getMoves(moveCandidates, squareMap, current, -1, -2);
        getMoves(moveCandidates, squareMap, current, 1, -2);
        getMoves(moveCandidates, squareMap, current, -1, 2);
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
            if (next != null && squareMap.containsKey(next)) {
                if (squareMap.get(next).isTaken()) {
                    if (squareMap.get(next).getCurrentPieceOnSquare().color.equals(this.color)) return;
                    candidates.add(next);
                    return;
                }
                candidates.add(next);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }

}
