package company.View;

import company.Board.BoardLocation;
import company.Board.Squares;
import company.Game.GameModel;
import company.Pieces.PieceColor;
import company.Player.AbstractPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class updating GUI board based on board object.
 */
public class GuiMove {

    private static final Logger logger = Logger.getLogger(GuiMove.class.getName());


    /**
     * Private constructor of GUIMove class.
     */
    private GuiMove(){}

    /**
     * Method to update GUI board.
     * @param player player making the move
     * @param from  source location
     * @param to    destination location
     * @param squarePanel   instance of squarePanel
     * @param gameModel     instance of squarePanel
     */
    public static void guiMove(AbstractPlayer player, BoardLocation from, BoardLocation to, SquarePanel squarePanel, GameModel gameModel){

        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                Squares squareToRedraw = gameModel.getBoard().getBoardArray()[j][i];
                BoardTile tileToRedraw = squarePanel.getBoardTileArray()[j][i];

                if (squareToRedraw.isTaken()){
                    //Updating GUI board based on current state board object
                    try {
                        tileToRedraw.removeAll();
                        String pieceNameForFile = squareToRedraw.getCurrentPieceOnSquare().getName();
                        String pieceColorForFile = squareToRedraw.getCurrentPieceOnSquare().getColor().equals(PieceColor.BLACK) ? "Black" : "White";
                        String path = "/" + pieceColorForFile + "_" + pieceNameForFile + ".png";
                        BufferedImage pieceImage = ImageIO.read(GuiMove.class.getResource(path));
                        //BufferedImage pieceImage = ImageIO.read(GuiMove.class.getClassLoader().getResourceAsStream("White_King.png"));
                        tileToRedraw.setPieceImageLabel(new JLabel(new ImageIcon(pieceImage)));
                        tileToRedraw.add(tileToRedraw.getPieceImageLabel());
                    } catch (Exception e) {
                        logger.log(Level.WARNING, e.toString());
                    }
                }else {
                    tileToRedraw.removeAll();
                    tileToRedraw.revalidate();
                    tileToRedraw.repaint();
                }
            }
        }



    }

    /**
     * Method to update GUI board.
     * @param squarePanel instance of the squarePanel
     * @param gameModel   instance of the board
     */
    public static void guiMove(SquarePanel squarePanel, GameModel gameModel){

        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                Squares squareToRedraw = gameModel.getBoard().getBoardArray()[j][i];
                BoardTile tileToRedraw = squarePanel.getBoardTileArray()[j][i];

                if (squareToRedraw.isTaken()){
                    //Updating GUI board based on current state board object
                    try {
                        tileToRedraw.removeAll();
                        String pieceNameForFile = squareToRedraw.getCurrentPieceOnSquare().getName();
                        String pieceColorForFile = squareToRedraw.getCurrentPieceOnSquare().getColor().equals(PieceColor.BLACK) ? "Black" : "White";
                        String path = "/" + pieceColorForFile + "_" + pieceNameForFile + ".png";
                        BufferedImage pieceImage = ImageIO.read(GuiMove.class.getResource(path));
                        //BufferedImage pieceImage = ImageIO.read(GuiMove.class.getClassLoader().getResourceAsStream("White_King.png"));
                        tileToRedraw.setPieceImageLabel(new JLabel(new ImageIcon(pieceImage)));
                        tileToRedraw.add(tileToRedraw.getPieceImageLabel());
                    } catch (IOException e) {
                        logger.log(Level.WARNING, e.toString());
                    }
                }else {
                    tileToRedraw.removeAll();
                    tileToRedraw.revalidate();
                    tileToRedraw.repaint();
                }
            }
        }
    }
}
