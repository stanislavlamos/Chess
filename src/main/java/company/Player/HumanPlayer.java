package company.Player;

import company.Pieces.PieceColor;

/**
 * Class representing Human player.
 */
public class HumanPlayer extends AbstractPlayer {

    /*
     * Constructor of HumanPlayer class.
     * @param playerName    name of the player inheriting this class
     * @param pieceColor    color of the pieces assigned to this player
     * @param playerType    type of the player
     * @param minutes       initial value of minutes for the player
     * @param seconds       initial value of seconds for the player
     */
    public HumanPlayer(String playerName, PieceColor playerPieceColor, PlayerType playerType, Integer minutes, Integer seconds){
        super(playerName, playerPieceColor, playerType, minutes, seconds);
    }
}
