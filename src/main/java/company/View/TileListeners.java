package company.View;

import company.Board.BoardLocation;
import company.Board.Squares;
import company.Game.GameModel;
import company.Game.GameStates;
import company.Pieces.MasterAbstractPiece;
import company.Pieces.PieaceType;
import company.Player.PlayerType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which represents mouseListener for every tile on the chess board.
 */
public class TileListeners implements MouseListener {
    private SquarePanel squarePanel;
    private GameModel gameModel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor of the TileListeners.
     * @param squarePanel instance of the squarePanel
     */
    public TileListeners(SquarePanel squarePanel){
        this.squarePanel = squarePanel;
        this.gameModel = squarePanel.getGameModel();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        BoardTile curTile = (BoardTile) e.getComponent();
        MasterAbstractPiece pieceOnTile = gameModel.getBoard().getLocationSquaresMap().get(curTile.getTileLocation()).getCurrentPieceOnSquare();

        //Gamestate is active
        if (gameModel.getGameState().equals(GameStates.ACTIVE)) {

            if (!(gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.HUMANPLAYER)) || gameModel.isPawnPromotion()) {
                return;
            }

            //Reverse highlight with right mouse click
            if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1 && squarePanel.getBoardTileWithBorder() != null) {
                reverseHighlight();
                squarePanel.getBoardTileWithBorder().setBorder(null);
                squarePanel.getBoardTileWithBorder().setBorderStatus(false);
                squarePanel.setBoardTileWithBorder(null);
            } else if (pieceOnTile != null && SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 && pieceOnTile.getColor().equals(gameModel.getPlayerOnTurn().getPlayerPieceColor())) {
                //Highlight possible moves with left mouse click
                if (squarePanel.getBoardTileWithBorder() != null) {
                    reverseHighlight();
                    BoardTile highlightedTile = squarePanel.getBoardTileWithBorder();
                    highlightedTile.setBorder(null);
                    squarePanel.setBoardTileWithBorder(highlightedTile);
                }
                curTile.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 5));
                squarePanel.setBoardTileWithBorder(curTile);
                highlightPossibleMoves(curTile);
            } else if ((curTile.isHighLighted() && SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 && squarePanel.getBoardTileWithBorder() != null)) {
                //Make a move to highlighted tiles
                if (gameModel.getBoard().getLocationSquaresMap().get(squarePanel.getBoardTileWithBorder().getTileLocation()).getCurrentPieceOnSquare().getColor().equals(gameModel.getPlayerOnTurn().getPlayerPieceColor())) {
                    MoveReceiver moveReceiver = new MoveReceiver(gameModel, curTile, squarePanel.getBoardTileArray());
                    moveProcess(moveReceiver.deliverMove());
                }
            }
        }

        //Handling move editor before game
        else if (gameModel.getGameState().equals(GameStates.PREGAMEMOVE)){
            PieceEditor pieceEditor = new PieceEditor();

            //Show dialog to select an piece you want to place on board
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1){
                curTile.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 5));
                pieceEditor.showDialog(gameModel, curTile, squarePanel);
                curTile.setBorder(null);
            }

            //Delete placed piece on board by right mouse click
            else if(SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1){
                gameModel.getBoard().getLocationSquaresMap().get(curTile.getTileLocation()).setCurrentPieceOnSquare(null);
                gameModel.getBoard().getLocationSquaresMap().get(curTile.getTileLocation()).setOccupation(false);
                curTile.removeAll();
                curTile.revalidate();
                curTile.repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    /**
     * Method to reverse highlight.
     */
    private void reverseHighlight(){
        MasterAbstractPiece pieceOnTile = gameModel.getBoard().getLocationSquaresMap().get(squarePanel.getBoardTileWithBorder().getTileLocation()).getCurrentPieceOnSquare();
        List<BoardLocation> possibleMoves = pieceOnTile.getValidMoves(gameModel.getBoard());

        if(possibleMoves.size() == 0){
            return;
        }

        //Iterate over possible moves of the piece and reverse its highlight
        for (BoardLocation location : possibleMoves){

            if (gameModel.getBoard().getLocationSquaresMap().get(location).isTaken()
                    && gameModel.getBoard().getLocationSquaresMap().get(location).getCurrentPieceOnSquare().getPieceType().equals(PieaceType.KING)){
                continue;
            }

            BoardTile tileToDeHighlight = squarePanel.getBoardTileArray()[location.getFile().ordinal()][location.getRank() - 1];
            if (tileToDeHighlight.getHighlightedLabel() != null){
                tileToDeHighlight.remove(tileToDeHighlight.getHighlightedLabel());
            }
            tileToDeHighlight.setHighlightedLabel(null);
            tileToDeHighlight.setIsHighLighted(false);
            tileToDeHighlight.validate();
            tileToDeHighlight.repaint();
        }
    }

    /**
     * Method to highlight possible moves on chess board.
     * @param selectedTile  tile on which user has clicked
     */
    private void highlightPossibleMoves(BoardTile selectedTile){
        MasterAbstractPiece pieceOnTile = gameModel.getBoard().getLocationSquaresMap().get(selectedTile.getTileLocation()).getCurrentPieceOnSquare();
        List<BoardLocation> possibleMoves = pieceOnTile.getValidMoves(gameModel.getBoard());

        if(possibleMoves.size() == 0){
            return;
        }

        //Iterating over the possible moves of the piece on selected tile
        for (BoardLocation location : possibleMoves){

            if (gameModel.getBoard().getLocationSquaresMap().get(location).isTaken()
                    && gameModel.getBoard().getLocationSquaresMap().get(location).getCurrentPieceOnSquare().getPieceType().equals(PieaceType.KING)){
                continue;
            }

            if (gameModel.getBoard().isCheck()){
                if (pieceOnTile.getPieceType().equals(PieaceType.KING)
                        && !gameModel.getBoard().getKingMovesToAvoidCheck().contains(location)){
                    continue;
                }

                else if (!pieceOnTile.getPieceType().equals(PieaceType.KING)
                        && !gameModel.getBoard().getMovesToAvoidCheck().contains(location)){
                    continue;
                }
            }

            //Highlighting possible tiles on board
            try {
                BoardTile tileToHighlight = squarePanel.getBoardTileArray()[location.getFile().ordinal()][location.getRank() - 1];
                String path = "/Ellipse_new.png";
                tileToHighlight.setIsHighLighted(true);
                BufferedImage pieceImage = ImageIO.read(this.getClass().getResource(path));
                JLabel dotToLabel = new JLabel(new ImageIcon(pieceImage));
                tileToHighlight.setHighlightedLabel(dotToLabel);
                tileToHighlight.add(dotToLabel);
            }catch (IOException exception){
                logger.log(Level.WARNING, exception.toString());
                break;
            }
        }
    }

    //Making the GUI move process
    private void moveProcess(BoardTile destinationTile) {
        BoardTile sourceTile = squarePanel.getBoardTileWithBorder();
        reverseHighlight();

        //Reversing highlight and tile with border
        squarePanel.getBoardTileWithBorder().setBorder(null);
        squarePanel.getBoardTileWithBorder().setBorderStatus(false);
        squarePanel.setBoardTileWithBorder(null);
        destinationTile.removeAll();
        sourceTile.removeAll();

        gameModel.getGameController().initiateMove(gameModel.getPlayerOnTurn(), sourceTile.getTileLocation(), destinationTile.getTileLocation(), squarePanel);
    }

    /**
     * Method to emptify the board before placing the pieces on the board.
     */
    private void emptifyBoard(){
        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                Squares square = gameModel.getBoard().getBoardArray()[j][i];
                square.setCurrentPieceOnSquare(null);
                square.setOccupation(false);
            }
        }
    }
}
