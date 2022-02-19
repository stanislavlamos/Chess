package company.Player;

import company.Pieces.PieceColor;

/*
 * Class for player playing over internet which inherits from AbstractPlayer class.
 */
public class NetPlayer extends AbstractPlayer {

    /*
     * Constructor of NetPlayer class.
     * @param playerName    name of the player inheriting this class
     * @param pieceColor    color of the pieces assigned to this player
     * @param playerType    type of the player
     */
    public NetPlayer(String playerName, PieceColor playerPieceColor, PlayerType playerType){
        super(playerName, playerPieceColor, playerType);
    }
}
