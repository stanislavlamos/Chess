package company.Board;

import company.Pieces.MasterAbstractPiece;

import java.io.Serializable;

/*
 * Class representing each square on the chess board.
 */
public class Squares implements Serializable {
    private final BoardLocation location;
    private boolean isTaken;
    private final SquareColor squareColor;
    private MasterAbstractPiece currentPieceOnSquare;

    /*
     * Constructor for the attributes of the square.
     * @param squareColor   color of the square
     * @param location  location of the square on the chess board
     */
    public Squares(SquareColor squareColor, BoardLocation location) {
        this.location = location;
        this.isTaken = false;
        this.squareColor = squareColor;
    }

    /*
     * Method for resetting square occupation.
     */
    public void resetSquareOccupation() {
        this.isTaken = false;
        this.currentPieceOnSquare = null;
    }

    /*
     * Getter for current piece located on the square.
     * @return  piece on the square
     */
    public MasterAbstractPiece getCurrentPieceOnSquare() {
        return currentPieceOnSquare;
    }

    /*
     * Setter for current piece on square.
     * @param currentPieceOnSquare  set the piece to new square on the chess board
     */
    public void setCurrentPieceOnSquare(MasterAbstractPiece currentPieceOnSquare) {
        this.currentPieceOnSquare = currentPieceOnSquare;
    }

    /*
     * Getter for location of the square.
     * @return  location of the square
     */
    public BoardLocation getLocation() {
        return location;
    }

    /*
     * Getter for occupancy of the square.
     * @return boolean indicating occupancy of the square
     */
    public boolean isTaken() {
        return isTaken;
    }

    /*
     * Setter for occupancy of the square.
     * @param new occupancy status
     */
    public void setOccupation(boolean taken) {
        isTaken = taken;
    }

    /*
     * Getter for color of the square.
     * @return  color of the square
     */
    public SquareColor getSquareColor() {
        return squareColor;
    }

    @Override
    public String toString() {
        return "Squares{" +
                "location=" + location +
                ", isTaken=" + isTaken +
                ", squareColor=" + squareColor +
                '}';
    }
}


