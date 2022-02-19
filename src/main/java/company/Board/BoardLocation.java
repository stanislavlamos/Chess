package company.Board;

import java.io.Serializable;
import java.util.Objects;

/*
 * Class representing location on the square board.
 */
public class BoardLocation implements Serializable {

    //@Serial
    //private static final long serialVersionUID = 1L;

    private final Integer rank;
    private final Files file;

    /*
     * Constructor for BoardLocation class attributes.
     * @param file  file location
     * @param rank  rank location
     */
    public BoardLocation(Files file, Integer rank){
        this.file = file;
        this.rank = rank;
    }

    /*
     * Getter for file location (x coordinate).
     * @return  file location
     */
    public Files getFile(){
        return this.file;
    }

    /*
     * Getter for rank location (y coordinate).
     * @return  rank location
     */
    public Integer getRank(){
        return this.rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardLocation)) return false;
        BoardLocation that = (BoardLocation) o;
        return Objects.equals(rank, that.rank) && file == that.file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, file);
    }

    @Override
    public String toString() {
        return "" + file + rank;
    }
}
