package company.Pieces;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Game.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the Pawn piece.
 */
public class Pawn extends MasterAbstractPiece {
    protected boolean firstMove;
    private boolean isEnPassant;
    BoardLocation entPassantLocation;

    /**
     * Constructor of the Pawn class.
     * @param color color of the Pawn
     */
    public Pawn(PieceColor color){
        super(color, 0);
        this.name = "Pawn";
        this.pieceTag = "P";
        this.firstMove = true;
        this.pieceType = PieaceType.PAWN;
        this.isEnPassant = false;
    }

    /**
     * Getter of the enPassant status.
     * @return status fo the enPassant move
     */
    public boolean isEnPassant() {
        return isEnPassant;
    }

    /**
     * Setter for enPassant move.
     * @param enPassant new enPassant status
     */
    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }

    /**
     * Getter to get enPassant location.
     * @return  enPassant location
     */
    public BoardLocation getEntPassantLocation() {
        return entPassantLocation;
    }

    /**
     * Setter for location of enPassant.
     * @param entPassantLocation    new location of enPassant move
     */
    public void setEntPassantLocation(BoardLocation entPassantLocation) {
        this.entPassantLocation = entPassantLocation;
    }

    /*
     * Getter for first move indicator of the Pawn.
     * @return  boolean indicating whether this is the first move of the pawn
     */
    public boolean isFirstMove() {
        return firstMove;
    }

    /*
     * Setter for first move attribute of the Pawn.
     * @param firstMove new value of first move attribute
     */
    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    @Override
    public List<BoardLocation> getValidMoves(Board board) {
        List<BoardLocation> moveCandidates = new ArrayList<>();
        BoardLocation newLocation;
        BoardLocation currentLocation = this.getSquares().getLocation();
        int offsetSmall = getColor().equals(PieceColor.BLACK) ? -1 : 1;
        int offsetBig = getColor().equals(PieceColor.BLACK)? -2 : 2;
        int doubleMoveChecker = getColor().equals(PieceColor.BLACK)? 1 : -1;

        if (firstMove){
            newLocation = LocationUtils.createBoardLocation(this.getSquares().getLocation(), 0, offsetBig);
            BoardLocation checkLocation = LocationUtils.createBoardLocation(this.getSquares().getLocation(), 0, offsetBig + doubleMoveChecker);

            if (newLocation != null && notOccupied(newLocation, board) && checkLocation != null && notOccupied(checkLocation, board)){
                moveCandidates.add(newLocation);
            }
        }

        newLocation = LocationUtils.createBoardLocation(this.getSquares().getLocation(), 0, offsetSmall);
        if (newLocation != null && notOccupied(newLocation, board)){
            moveCandidates.add(newLocation);
        }

        int tmpOffsetX = 1;
        for (int i = 0; i < 2; i++){
            newLocation = LocationUtils.createBoardLocation(this.getSquares().getLocation(), tmpOffsetX, offsetSmall);

            if (newLocation != null && canTakeDiagonal(newLocation, board)){
                moveCandidates.add(newLocation);
            }

            tmpOffsetX = -1;
        }

        //Checking enPassant for white Pawn
        if (currentLocation.getRank().equals(5) && this.color.equals(PieceColor.WHITE)){
            Integer offset = 1;
            for (Integer i = 0; i < 2; i++){
                BoardLocation prospectedLocation = LocationUtils.createBoardLocation(currentLocation, offset, 0);
                MasterAbstractPiece pieceOnProspectedLocation = prospectedLocation != null ? board.getLocationSquaresMap().get(prospectedLocation).getCurrentPieceOnSquare() : null;
                if (pieceOnProspectedLocation != null && !(pieceOnProspectedLocation.getColor().equals(this.color)) && pieceOnProspectedLocation.getPieceType().equals(PieaceType.PAWN)){
                    Move lastMove = board.getMoveHistory().size() != 0 ? board.getMoveHistory().get(board.getMoveHistory().size() - 1) : null;
                    if (lastMove != null && lastMove.getMovingPiece().getPieceType().equals(PieaceType.PAWN) && (lastMove.getMovingPiece().getColor().equals(PieceColor.BLACK)) && (lastMove.getFromLocation().getRank() - lastMove.getToLocation().getRank() == 2) && lastMove.getToLocation().equals(prospectedLocation)){
                        this.entPassantLocation = LocationUtils.createBoardLocation(lastMove.getToLocation(), 0, 1);
                        if (!board.getLocationSquaresMap().get(this.entPassantLocation).isTaken()){
                            moveCandidates.add(this.entPassantLocation);
                            this.isEnPassant = true;
                        }
                    }
                }
                offset = -1;
            }
        }

        //Checking enPassant for black Pawn
        else if (currentLocation.getRank().equals(4) && this.color.equals(PieceColor.BLACK)){
            Integer offset = 1;
            for (Integer i = 0; i < 2; i++){
                BoardLocation prospectedLocation = LocationUtils.createBoardLocation(currentLocation, offset, 0);
                MasterAbstractPiece pieceOnProspectedLocation = prospectedLocation != null ? board.getLocationSquaresMap().get(prospectedLocation).getCurrentPieceOnSquare() : null;
                if (pieceOnProspectedLocation != null && !(pieceOnProspectedLocation.getColor().equals(this.color)) && pieceOnProspectedLocation.getPieceType().equals(PieaceType.PAWN)){
                    Move lastMove = board.getMoveHistory().size() != 0 ? board.getMoveHistory().get(board.getMoveHistory().size() - 1) : null;
                    if (lastMove != null && lastMove.getMovingPiece().getPieceType().equals(PieaceType.PAWN) && (lastMove.getMovingPiece().getColor().equals(PieceColor.WHITE)) &&(lastMove.getFromLocation().getRank() - lastMove.getToLocation().getRank() == -2) && lastMove.getToLocation().equals(prospectedLocation)){
                        this.entPassantLocation = LocationUtils.createBoardLocation(lastMove.getToLocation(), 0, -1);
                        if (!board.getLocationSquaresMap().get(this.entPassantLocation).isTaken()){
                            moveCandidates.add(this.entPassantLocation);
                            this.isEnPassant = true;
                        }
                    }
                }
                offset = -1;
            }
        }
        return moveCandidates;
    }

    /*
     * Method checking occupancy of the given square on the chess board.
     * @param desiredLocation   desired location of the piece move
     * @param board instance of the chess board
     * @return  boolean indicating whether the square on lon desired is not occupied
     */
    private boolean notOccupied(BoardLocation desiredLocation, Board board){
        boolean retVal = true;
        if(board.getLocationSquaresMap().containsKey(desiredLocation) && board.getLocationSquaresMap().get(desiredLocation).isTaken()){
            retVal = false;
        }
        return retVal;
    }

    /*
     * Checking whether Pawn can go diagonal.
     * @param desiredLocation   desired location of the piece move
     * @param board instance of the board
     * @return  boolean indicating whether Pawn can go diagonal
     */
    private boolean canTakeDiagonal(BoardLocation desiredLocation, Board board){
        boolean retVal = true;

        if (!board.getLocationSquaresMap().containsKey(desiredLocation)){
            return false;
        }


        if (board.getLocationSquaresMap().get(desiredLocation).getCurrentPieceOnSquare() == null
                && !board.getLocationSquaresMap().get(desiredLocation).isTaken()){
            return false;
        }

        if (board.getLocationSquaresMap().get(desiredLocation).getCurrentPieceOnSquare().color.equals(this.color)){
            retVal = false;
        }
        return retVal;
    }
}
