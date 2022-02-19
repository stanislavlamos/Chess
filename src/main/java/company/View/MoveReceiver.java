package company.View;

import company.Game.GameModel;
import company.Player.AbstractPlayer;
import company.Player.PlayerType;

/**
 * Class which delivers move to GUI
 */
public class MoveReceiver {
    private GameModel gameModel;
    private BoardTile selectedTile;
    private BoardTile[][] boardTileArray;

    /**
     * Constructor of the MoveReceiver.
     * @param gameModel instance if the gameModel
     * @param selectedTile  instance of the selected tile
     * @param boardTileArray    array of board tiles
     */
    public MoveReceiver(GameModel gameModel, BoardTile selectedTile, BoardTile[][] boardTileArray){
        this.gameModel = gameModel;
        this.selectedTile = selectedTile;
        this.boardTileArray = boardTileArray;
    }

    /**
     * Method to deliver moves for GUI.
     * @return  destination tile to for the move
     */
    public BoardTile deliverMove(){
        BoardTile destinationTile; //Writing this to prevent Java from throwing errors
        AbstractPlayer playerOnTurn = gameModel.getPlayerOnTurn();

        //Checking whether the player is Human player or not
        if (playerOnTurn.getPlayerType().equals(PlayerType.HUMANPLAYER) && this.selectedTile != null){
            destinationTile = this.selectedTile;
        } else{
            //Logic for receiving move and updated board from NetPlayer
            //Writing this to prevent Java from throwing errors
            destinationTile = boardTileArray[0][0];
        }
        return destinationTile;
    }
}
