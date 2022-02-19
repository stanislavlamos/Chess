package company.Pieces;

import company.Board.BoardLocation;
import company.Board.Files;

import java.io.Serializable;

/*
 * Class with methods for location support.
 */
public class LocationUtils implements Serializable {
    private static final Files[] files = Files.values();

    /*
     * Private constructor of AbstractPlayer class.
     */
    private LocationUtils(){}

    /*
     * Building new board location from offset.
     * @param current   current location on the chess board
     * @param offsetX   offset for x location
     * @param offsetY   offset for y location
     * @return  new location on chess board with incremented offset
     */
    public static BoardLocation createBoardLocation(BoardLocation current, Integer offsetX, Integer offsetY){

        if (current.equals(null)){
            return null;
        }

        int newX = current.getFile().ordinal() + offsetX;
        int newY = current.getRank() + offsetY;

        if ((newX >= files.length || newX < 0) || (newY > 8 || newY <= 0)) {
            return null;
        }
        else {
            return new BoardLocation(files[newX], newY);
        }
    }

}
