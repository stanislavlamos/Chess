package company.Player;

import company.Pieces.PieceColor;

import java.io.Serial;
import java.io.Serializable;

/*
 * Abstract class for every Player.
 */
public abstract class AbstractPlayer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer numberOfpieces;
    private final Integer START_PIECE_NUM = 16;
    private String playerName;
    private final PieceColor playerPieceColor;
    private Integer clockValMinutes;
    private Integer clockValSeconds;
    private PlayerType playerType;

    /*
     * Constructor of AbstractPlayer class.
     * @param playerName    name of the player inheriting this class
     * @param pieceColor    color of the pieces assigned to this player
     */
    public AbstractPlayer(String playerName, PieceColor playerPieceColor, PlayerType playerType){
        this.numberOfpieces = START_PIECE_NUM;
        this.playerName = playerName;
        this.playerPieceColor = playerPieceColor;
        this.playerType = playerType;

        this.clockValSeconds = -1;
        this.clockValMinutes = -1;
    }

    public AbstractPlayer(String playerName, PieceColor playerPieceColor, PlayerType playerType, Integer clockValMinutes, Integer clockValSeconds){
        this.numberOfpieces = START_PIECE_NUM;
        this.playerName = playerName;
        this.playerPieceColor = playerPieceColor;
        this.playerType = playerType;
        this.clockValMinutes = clockValMinutes;
        this.clockValSeconds = clockValSeconds;
    }

    /*
     * Getter for number of pieces attribute.
     * @return  number of pieces for this player
     */
    public Integer getNumberOfpieces() {
        return numberOfpieces;
    }

    /*
     * Setter for number of pieces attribute.
     */
    public void setNumberOfpieces(Integer numberOfpieces) {
        this.numberOfpieces = numberOfpieces;
    }

    /*
     * Getter player's name attribute.
     * @return  name of the player
     */
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /*
     * Getter for color of the players' pieces.
     * @return  color of the players' pieces
     */
    public PieceColor getPlayerPieceColor() {
        return playerPieceColor;
    }

    /**
     * Getter for type of the player.
     * @return type of the player
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Getter for remaining minutes of the player.
     * @return  remaining minutes of the player
     */
    public Integer getClockValMinutes() {
        return clockValMinutes;
    }

    /**
     * Getter for remaining seconds of the player.
     * @return  remaining seconds of the player
     */
    public Integer getClockValSeconds() {
        return clockValSeconds;
    }

    /**
     * Setter for minutes of the player.
     * @param clockValMinutes   new value of minutes
     */
    public void setClockValMinutes(Integer clockValMinutes) {
        this.clockValMinutes = clockValMinutes;
    }

    /**
     * Setter for seconds of the player.
     * @param clockValSeconds   new value of seconds
     */
    public void setClockValSeconds(Integer clockValSeconds) {
        this.clockValSeconds = clockValSeconds;
    }
}
