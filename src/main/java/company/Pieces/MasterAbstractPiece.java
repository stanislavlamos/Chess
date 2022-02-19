package company.Pieces;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Board.Squares;

import java.io.Serializable;
import java.util.List;

/*
 * Master abstract class for every piece on chess board.
 */
public abstract class MasterAbstractPiece implements Serializable {
    protected String name;
    private Squares squares;
    protected PieceColor color;
    protected String pieceTag;
    protected PieaceType pieceType;
    private Integer stepCounter;

    /*
     * Constructor of MasterbAbstractPiece class.
     * @param pieceColor    color of the piece
     * @param stepCounter   initial number of moves
     */
    public MasterAbstractPiece(PieceColor color, Integer stepCounter){
        this.color = color;
        this.stepCounter = stepCounter;
    }

    /*
     * Getter for piece tag attribute.
     * @return  piece tag of the attribute
     */
    public String getPieceTag() {
        return pieceTag;
    }

    /*
     * Getter for name of the piece.
     * @return  name of the piece
     */
    public String getName() {
        return name;
    }

    /*
     * Getter for square position of the piece.
     * @return  square position of the piece
     */
    public Squares getSquares() {
        return squares;
    }

    /*
     * Setter for square position of this piece.
     * @param squares   square position
     */
    public void setSquares(Squares squares) {
        this.squares = squares;
    }

    /*
     * Getter for color of the piece.
     * @return  color of the piece
     */
    public PieceColor getColor() {
        return color;
    }

    /*
     * Setter for the color of the piece.
     * @param color    color of the piece
     */
    public void setColor(PieceColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "MasterAbstractPiece{" +
                "name='" + name + '\'' +
                ", squares=" + squares +
                ", color=" + color +
                ", pieceTag='" + pieceTag + '\'' +
                ", pieceType=" + pieceType +
                ", stepCounter=" + stepCounter +
                '}';
    }

    /*
     * Checking whether the projected move is valid for the piece.
     * @param location  location of the projected move on board
     * @param possibleMoves list of possible moves for the piece
     * @return  boolean indicating whether the move is valid or not for the piece
     */
    public boolean checkMoveValidity(BoardLocation location, List<BoardLocation> possibleMoves){
        boolean retVal = false;

        if (possibleMoves.contains(location)){
            retVal = true;
        }
        return retVal;
    }


    /*
     * Abstract class generating list of valid move for the piece.
     * @param board instance of the chess board
     * @return  list of valid moves for the piece
     */
    public abstract List<BoardLocation> getValidMoves(Board board);

    /**
     * Getter for piece type.
     * @return type of the piece
     */
    public PieaceType getPieceType() {
        return pieceType;
    }

    /**
     * Getter for number of moves the piece has made.
     * @return  number of moves the piece has made
     */
    public Integer getStepCounter() {
        return stepCounter;
    }

    /**
     * Setter for step counter.
     * @param stepCounter   new number of steps
     */
    public void setStepCounter(Integer stepCounter) {
        this.stepCounter = stepCounter;
    }
}
