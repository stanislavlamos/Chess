package company.Player;

import company.Pieces.PieceColor;

/**
 * Class representing Computer player.
 */
public class ComputerPlayer extends AbstractPlayer {

    /**
     * Constructor of the Computer player class.
     * @param playerName    name of the player
     * @param playerPieceColor  color of players pieces
     * @param playerType    type of the player
     */
    public ComputerPlayer(String playerName, PieceColor playerPieceColor, PlayerType playerType){
        super(playerName, playerPieceColor, playerType);
    }
}
