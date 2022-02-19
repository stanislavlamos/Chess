package company.View;

import company.Board.BoardLocation;
import company.Board.SquareColor;
import company.Game.GameController;
import company.Game.GameModel;
import company.Pieces.PieceColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing board tile on the chess board.
 */
public class BoardTile extends JPanel{
    private BoardLocation tileLocation;
    private SquareColor tileColor;
    private final Dimension BOARDTILE_SIZE = new Dimension(10, 10);
    private GameModel gameModel;
    private GameController gameController;
    private JLabel pieceImageLabel;
    private boolean isHighLighted;
    private JLabel highlightedLabel;
    private boolean hasBorder;
    private final Logger logger = Logger.getLogger(BoardTile.class.getName());


    /**
     * Cosntructor of the Board Tile class.
     * @param tileLocation  location of the tile on the chess board
     * @param colorIndex    color of the square
     * @param gameModel     instance of the gameModel
     */
    public BoardTile(BoardLocation tileLocation, SquareColor colorIndex, GameModel gameModel){
        super(new GridBagLayout());
        this.tileColor = colorIndex;
        this.tileLocation = tileLocation;
        this.gameModel = gameModel;
        this.gameController = gameModel.getGameController();
        this.isHighLighted = false;
        this.hasBorder = false;

        setPreferredSize(BOARDTILE_SIZE);
        determineColor();
        determinePieceImage();
        setVisible(true);
        validate();
    }

    /**
     * Method to determine color of the Tile.
     */
    private void determineColor(){
        if (tileColor.equals(SquareColor.GREEN)){
            this.setBackground(new Color(118, 150, 86));
        }
        else {
            this.setBackground(new Color(238, 238, 210));
        }
    }

    /**
     * Method to determine the actual image of the piece on tile.
     */
    private void determinePieceImage(){
        removeAll();
        String pieceNameForFile;
        String pieceColorForFile;

        //Checking whether the tile is taken or not
        //If so, open the corresponding image
        if (gameModel.getBoard().getLocationSquaresMap().get(this.tileLocation).isTaken()){
            try{
                pieceNameForFile = gameModel.getBoard().getLocationSquaresMap().get(this.tileLocation).getCurrentPieceOnSquare().getName();
                pieceColorForFile = gameModel.getBoard().getLocationSquaresMap().get(this.tileLocation).getCurrentPieceOnSquare().getColor().equals(PieceColor.BLACK) ? "Black" : "White";
                String path = "/" + pieceColorForFile + "_" + pieceNameForFile + ".png";
                BufferedImage pieceImage = ImageIO.read(this.getClass().getResource(path));
                //BufferedImage pieceImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("White_King.png"));
                this.pieceImageLabel = new JLabel(new ImageIcon(pieceImage));
                add(this.pieceImageLabel);
            }catch (IOException e){
                logger.log(Level.WARNING, e.toString());
            }
        }
    }

    /**
     * Getter for tile location on chess board.
     * @return  tile location on chess board
     */
    public BoardLocation getTileLocation() {
        return tileLocation;
    }

    @Override
    public String toString() {
        return "BoardTile{" +
                "tileLocation=" + tileLocation +
                ", tileColor=" + tileColor +
                ", gameModel=" + gameModel +
                ", isHighLighted=" + isHighLighted +
                '}';
    }

    /**
     * Method to check whether the tile is selected by the mouse click.
     * @return boolean indicating whether the tile is selected by the mouse click
     */
    public boolean hasBorder() {
        return hasBorder;
    }

    /**
     * Method to selection of the tile.
     * @param hasBorder state of tile selection
     */
    public void setBorderStatus(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    /**
     * Method checking whether the tile is legal move and is selected.
     * @return  highlight status based on legal moves
     */
    public boolean isHighLighted() {
        return isHighLighted;
    }

    /**
     * Setter for highlight status based on legal moves.
     * @param highLighted new highlight status
     */
    public void setIsHighLighted(boolean highLighted) {
        isHighLighted = highLighted;
    }

    /**
     * Method to get the higlighted label.
     * @return  higlighted label
     */
    public JLabel getHighlightedLabel() {
        return highlightedLabel;
    }

    /**
     * Setter for highlighted label.
     * @param highlightedLabel  new highlighted label
     */
    public void setHighlightedLabel(JLabel highlightedLabel) {
        this.highlightedLabel = highlightedLabel;
    }

    /**
     * Get image of the piece.
     * @return  image of the piece on board tile
     */
    public JLabel getPieceImageLabel() {
        return pieceImageLabel;
    }

    /**
     * Setter for image label of the piece.
     * @param pieceImageLabel   image label of the piece
     */
    public void setPieceImageLabel(JLabel pieceImageLabel) {
        this.pieceImageLabel = pieceImageLabel;
    }
}
