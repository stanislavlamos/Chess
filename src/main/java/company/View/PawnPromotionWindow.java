package company.View;

import company.Board.Board;
import company.Game.GameModel;
import company.Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Class which represents pawn promotion window.
 */
public class PawnPromotionWindow extends JFrame {
    private GameModel gameModel;
    private JRadioButton checkboxOne = new JRadioButton ("Bishop" ,false);
    private JRadioButton checkboxTwo = new JRadioButton("Rook", false);
    private JRadioButton checkboxThree = new JRadioButton("Knight", false);
    private JRadioButton checkboxFour = new JRadioButton("Queen", false);
    private JButton confirmButton = new JButton("Confirm");
    private Dimension WINDOW_SIZE = new Dimension(500, 100);
    private String pieceToPromote;
    private BoardTile destinationTile;
    private final Dimension INITIAL_WINDOW_POSITION = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Constructor of the pawn promotion window.
     * @param gameModel instance of the gameModel
     * @param destinationTile   destination tile on the chess board
     */
    public PawnPromotionWindow(GameModel gameModel, BoardTile destinationTile){
        this.gameModel = gameModel;
        this.destinationTile = destinationTile;

        render();
        validate();
    }

    /**
     * Method to render pawnPromotion window.
     */
    private void render(){
        //Adjusting frame's settings
        setTitle("Select piece to which you wish to promote your Pawn");
        setLayout(new GridLayout());
        setSize(WINDOW_SIZE);

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(checkboxOne);
        radioButtonGroup.add(checkboxTwo);
        radioButtonGroup.add(checkboxThree);
        radioButtonGroup.add(checkboxFour);
        add(checkboxOne);
        add(checkboxTwo);
        add(checkboxThree);
        add(checkboxFour);

        confirmButton.addMouseListener(new MouseListenerImpl(this));
        add(confirmButton);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setLocation(INITIAL_WINDOW_POSITION.width/2-this.getSize().width/2, INITIAL_WINDOW_POSITION.height/2-this.getSize().height/2);
        setVisible(true);
    }

    /**
     * Inner class which represents implementation of the mouse listener.
     */
    protected class MouseListenerImpl implements MouseListener{
        private JFrame window;

        /**
         * Constructor of the Mouse listener class.
         * @param window    instance of the pawn promotion window
         */
        public MouseListenerImpl(JFrame window){
            this.window = window;
        }


        @Override
        public void mouseClicked(MouseEvent e) {
            if (checkboxOne.isSelected()){
                pieceToPromote = "Bishop";
            }

            else if (checkboxTwo.isSelected()){
                pieceToPromote = "Rook";
            }

            else if (checkboxThree.isSelected()){
                pieceToPromote = "Knight";
            }

            else if (checkboxFour.isSelected()){
                pieceToPromote = "Queen";
            }
            window.setAlwaysOnTop(false);
            window.dispose();
            makePawnPromotion();
        }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }

    /**
     * Method to make pawn promotion.
     */
    private void makePawnPromotion(){
        String piecePromoteTag = "";
        destinationTile.removeAll();
        PieceColor pieceToPromoteColor = gameModel.getBoard().getLocationSquaresMap().get(destinationTile.getTileLocation()).getCurrentPieceOnSquare().getColor();
        MasterAbstractPiece pieceToSet;
        Board board = gameModel.getBoard();

        if (pieceToPromote.equals("Rook")){
            pieceToSet = new Rook(pieceToPromoteColor);
        }

        else if (pieceToPromote.equals("Bishop")){
            pieceToSet = new Bishop(pieceToPromoteColor);
        }

        else if (pieceToPromote.equals("Knight")){
            pieceToSet = new Knight(pieceToPromoteColor);
        }

        else{
            pieceToSet = new Queen(pieceToPromoteColor);
        }

        //Updating and redrawing the board
        piecePromoteTag = pieceToSet.getPieceTag();
        gameModel.getBoard().handlePawnPromotionPgn(piecePromoteTag);

        board.getLocationSquaresMap().get(destinationTile.getTileLocation()).setCurrentPieceOnSquare(pieceToSet);
        gameModel.setPawnPromotion(false);
        gameModel.setBoard(board);
        gameModel.getGameController().updateBoard(destinationTile.getTileLocation(), destinationTile.getTileLocation());
        GuiMove.guiMove(gameModel.getGameController().getSquarePanel(), gameModel);
        gameModel.changePlayerOnTurn();
    }
}
