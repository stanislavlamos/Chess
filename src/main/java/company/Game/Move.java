package company.Game;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Pieces.MasterAbstractPiece;
import company.Player.AbstractPlayer;

import java.io.Serializable;

/*
 * Class representing player's chess move.
 */
public class Move implements Serializable {

    private AbstractPlayer player;
    private BoardLocation from;
    private BoardLocation to;
    private MasterAbstractPiece movingPiece;
    private Board board;
    private String pgnNotation;

    /*
     * Constructor of Move class attributes
     * @param player    instance of the player making the move
     * @param from  start location of the desired piece for move
     * @param to   final destination of the moving piece
     * @param board instance of the chess board
     */
    public Move(AbstractPlayer player, BoardLocation from, BoardLocation to, Board board){
        this.player = player;
        this.from = from;
        this.to = to;
        this.board = board;
        this.movingPiece = board.getLocationSquaresMap().get(from).getCurrentPieceOnSquare();
    }

    /**
     * Constructor of Move class attributes
     * @param player    instance of the player making the move
     * @param from  start location of the desired piece for move
     * @param to   final destination of the moving piece
     * @param movingPiece piece on the move
     * @param pgnNotation   PGN notation of the move
     */
    public Move(AbstractPlayer player, BoardLocation from, BoardLocation to, MasterAbstractPiece movingPiece, String pgnNotation){
        this.player = player;
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.pgnNotation = pgnNotation;
    }

    /*
     * Getter for player making the move
     * @return  player making the move
     */
    public AbstractPlayer getPlayer() {
        return player;
    }

    /*
     * Getter for starting board location of the move
     * @return  starting board location of the move
     */
    public BoardLocation getFromLocation() {
        return from;
    }

    /*
     * Getter for final location of the move
     * @return  final location of the move
     */
    public BoardLocation getToLocation() {
        return to;
    }

    /*
     * Getter for the piece being moved
     * @return  piece being moved
     */
    public MasterAbstractPiece getMovingPiece() {
        return movingPiece;
    }

    /**
     * Method to get PGN notation of the move
     * @return String   PGN notation of the move
     */
    public String getPgnNotation() {
        return pgnNotation;
    }

    /**
     * Setter for PGN notation of the move
     * @param pgnNotation   new PGN notation of the move
     */
    public void setPgnNotation(String pgnNotation) {
        this.pgnNotation = pgnNotation;
    }

    @Override
    public String toString() {
        return "Move{" +
                "player=" + player +
                ", from=" + from +
                ", to=" + to +
                ", movingPiece=" + movingPiece +
                ", pgnNotation='" + pgnNotation + '\'' +
                '}';
    }

    /**
     * Method to return formatted PGN notation of the move
     * @return  String  formatted PGN notation of the move
     */
    public String pgnToString(){
        return "PGN: " + this.pgnNotation;
    }
}
