package company.View;

import company.Board.BoardLocation;
import company.Board.Files;
import company.Board.SquareColor;
import company.Game.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Class representing squarePanel which holds tiles of the chess board.
 */
public class SquarePanel extends JPanel{
    private GameModel gameModel;
    private final BoardTile[][] boardTileArray;
    private final Integer BOARD_WIDTH = 8;
    private final Integer BOARD_HEIGHT = 8;
    private final Dimension BOARD_PANEL_SIZE = new Dimension(800, 800);
    private ArrayList<BoardTile> highLightedBoardTilesArray;
    private BoardTile boardTileWithBorder;

    /**
     * Constructor of the squarePanel class.
     * @param gameModel instance of the gameModel
     */
    public SquarePanel(GameModel gameModel){
        super(new GridLayout(8, 8));
        boardTileArray = new BoardTile[BOARD_WIDTH][BOARD_HEIGHT];
        this.gameModel = gameModel;
        this.highLightedBoardTilesArray = new ArrayList<>();
        this.boardTileWithBorder = null;

        fillSquarePanel();
        handleMouseInput();
    }

    /**
     * Method to fill squarePanel with board tiles.
     */
    private void fillSquarePanel(){
        Integer colorIndex = 0;
        //Iterating over the board and ading tiles to boardtile array
        for (Integer i = 0; i < BOARD_HEIGHT; i++) {
            for (Integer j = 0; j < BOARD_WIDTH; j++) {
                SquareColor currentColor = colorIndex % 2 == 0 ? SquareColor.GREEN : SquareColor.YELLOW;
                BoardTile tile = new BoardTile(new BoardLocation(Files.values()[j], i + 1), currentColor, gameModel);
                boardTileArray[j][i] = tile;
                colorIndex++;
            }
            colorIndex++;
        }

        //Adding tiles to squarePanel
        for(int i = BOARD_HEIGHT - 1;  i >= 0; i--) {
            for(int j = 0; j < BOARD_WIDTH; j++) {
                BoardTile tileToAdd = boardTileArray[j][i];
                add(tileToAdd);
            }
        }
        setPreferredSize(BOARD_PANEL_SIZE);
        setVisible(true);
        validate();
    }

    //Method which adds mouseListener to every board tile on chess board
    private void handleMouseInput() {
        //Iterating over the board and adding mouseListener to every board tile on chess board
        for (Integer i = 0; i < BOARD_HEIGHT; i++) {
            for (Integer j = 0; j < BOARD_WIDTH; j++) {
                BoardTile currentTile = boardTileArray[j][i];
                TileListeners listeners = new TileListeners(this);
                currentTile.addMouseListener(listeners);
                currentTile.addNotify();
            }
        }
    }

    /**
     * Getter for gameModel.
     * @return instance of the gameModel
     */
    public GameModel getGameModel() {
        return gameModel;
    }

    /**
     * Getter for boardTile array.
     * @return  array with tiles from chess board
     */
    public BoardTile[][] getBoardTileArray() {
        return boardTileArray;
    }

    /**
     * Getter for array with highlighted board tiles.
     * @return array with highlighted board tiles
     */
    public ArrayList<BoardTile> getHighLightedBoardTilesArray() {
        return highLightedBoardTilesArray;
    }

    /**
     * Setter for array with highlighted board tiles.
     * @param highLightedBoardTilesArray new instance of highLightedBoardTilesArray
     */
    public void setHighLightedBoardTilesArray(ArrayList<BoardTile> highLightedBoardTilesArray) {
        this.highLightedBoardTilesArray = highLightedBoardTilesArray;
    }

    /**
     * Getter for tile selected by mouse.
     * @return  tile selected by mouse
     */
    public BoardTile getBoardTileWithBorder() {
        return boardTileWithBorder;
    }

    /**
     * Setter for tile selected with mouse.
     * @param boardTileWithBorder new instance of boardTileWithBorder
     */
    public void setBoardTileWithBorder(BoardTile boardTileWithBorder) {
        this.boardTileWithBorder = boardTileWithBorder;
    }
}
