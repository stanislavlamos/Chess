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
 * Class representing the King piece.
 */
public class King extends MasterAbstractPiece {

    private static final transient Logger logger = Logger.getLogger(King.class.getName());

    /**
     * Constructor of the King class.
     * @param color color of the King
     */
    public King(PieceColor color){
        super(color, 0);
        this.name = "King";
        this.pieceTag = "K";
        this.pieceType = PieaceType.KING;
    }

    /**
     * Constructor of the King class.
     * @param color color of the King
     * @param square    square position of the King
     */
    public King(PieceColor color, Squares square){
        super(color, 0);
        this.name = "King";
        this.pieceTag = "K";
        this.setSquares(square);
        this.pieceType = PieaceType.KING;
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

        getMoves(moveCandidates, squareMap, current, 1, 0);
        getMoves(moveCandidates, squareMap, current, -1, 0);
        getMoves(moveCandidates, squareMap, current, 0, 1);
        getMoves(moveCandidates, squareMap, current, 0, -1);
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
            if (next != null && squareMap.containsKey(next)){
                if (squareMap.get(next).isTaken() && !squareMap.get(next).getCurrentPieceOnSquare().getColor().equals(this.color)) {
                    candidates.add(next);
                }

                else if (!squareMap.get(next).isTaken()){
                    candidates.add(next);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }

}
