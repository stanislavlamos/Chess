package company.Pieces;


import company.Board.Board;
import company.Board.BoardLocation;
import company.Board.Squares;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rook extends MasterAbstractPiece {

    private static final transient Logger logger = Logger.getLogger(Rook.class.getName());

    /**
     * Construcotr of the Rook class.
     * @param color color of the Rook
     */
    public Rook(PieceColor color){
        super(color, 0);
        this.name = "Rook";
        this.pieceTag = "R";
        this.pieceType = PieaceType.ROOK;
    }

    /**
     * Constructor of the Rook class.
     * @param color color of the Rook
     * @param square square location of the Rook
     */
    public Rook(PieceColor color, Squares square){
        super(color, 0);
        this.name = "Rook";
        this.pieceTag = "R";
        this.setSquares(square);
        this.pieceType = PieaceType.ROOK;
    }

    @Override
    public List<BoardLocation> getValidMoves(Board board) {
        List<BoardLocation> moveCandidates = new ArrayList<>();
        Map<BoardLocation, Squares> squareMap = board.getLocationSquaresMap();
        BoardLocation current = this.getSquares().getLocation();
        getFileCandidates(moveCandidates, squareMap, current, -1);
        getFileCandidates(moveCandidates, squareMap, current, 1);
        getRankCandidates(moveCandidates, squareMap, current, -1);
        getRankCandidates(moveCandidates, squareMap, current, 1);
        return moveCandidates;
    }

    /**
     * Method to get rank candidates for Rook valid moves.
     * @param moveCandidates list of move candidates
     * @param squareMap     location to square map of the chess board
     * @param current       current location of the Rook
     * @param offset        given offset to find valid moves
     */
    private void getRankCandidates(List<BoardLocation> moveCandidates, Map<BoardLocation, Squares> squareMap, BoardLocation current, int offset){
        try {
            BoardLocation next = LocationUtils.createBoardLocation(current, 0, offset);
            //Iterating over possible valid moves based on given rank offset
            while (next != null && squareMap.containsKey(next)) {
                if (squareMap.get(next).isTaken()) {
                    if (squareMap.get(next).getCurrentPieceOnSquare().color.equals(this.color)) {
                        break;
                    }
                    moveCandidates.add(next);
                    break;
                }
                moveCandidates.add(next);
                next = LocationUtils.createBoardLocation(next, 0, offset);
            }
        } catch (Exception e) {
           logger.log(Level.WARNING, e.toString());
        }
    }

    /**
     * Method to get file candidates for Rook valid moves.
     * @param moveCandidates list of move candidates
     * @param squareMap     location to square map of the chess board
     * @param current       current location of the Rook
     * @param offset        given offset to find valid moves
     */
    private void getFileCandidates(
            List<BoardLocation> moveCandidates,
            Map<BoardLocation, Squares> squareMap,
            BoardLocation current,
            int offset) {
        try {
            BoardLocation next = LocationUtils.createBoardLocation(current, offset, 0);
            //Iterating over possible valid moves based on given File offset
            while (next != null && squareMap.containsKey(next)) {
                if (squareMap.get(next).isTaken()) {
                    if (squareMap.get(next).getCurrentPieceOnSquare().color.equals(this.color)) {
                        break;
                    }
                    moveCandidates.add(next);
                    break;
                }
                moveCandidates.add(next);
                next = LocationUtils.createBoardLocation(next, offset, 0);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }
}
