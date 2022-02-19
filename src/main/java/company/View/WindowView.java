package company.View;

import company.Board.Squares;
import company.Game.GameController;
import company.Game.GameModel;
import company.Game.GameStates;
import company.Pieces.MasterAbstractPiece;
import company.Pieces.PieaceType;
import company.Pieces.PieceColor;
import company.Player.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for rendering the GUI window.
 */
public class WindowView extends JFrame{
    private SquarePanel squarePanel;
    private GameController gameController;
    private GameModel gameModel;
    private final Dimension WINDOW_SIZE = new Dimension(1100, 800);
    private final Dimension INITIAL_WINDOW_POSITION = Toolkit.getDefaultToolkit().getScreenSize();
    private boolean movePiecesBeforeGame;
    private JButton startGameAfterMovingPieces;
    private JFrame gameWindow;
    private GameWindowPanel gameWindowPanel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor of the windowView class.
     * @param gameModel instance of the gameModel
     * @param movePiecesBeforeGame  indicator whether user wants to move pieces before game
     */
    public WindowView(GameModel gameModel, Boolean movePiecesBeforeGame){
        super("Chess game");
        this.gameModel = gameModel;
        this.movePiecesBeforeGame = movePiecesBeforeGame;
        this.gameController = gameModel.getGameController();

        this.gameWindow = this;

        this.squarePanel = new SquarePanel(this.gameModel);
        renderWindow();
        this.add(squarePanel, BorderLayout.WEST);
        this.gameModel.setWindowView(this);
        determineGameStart();
        validate();
    }

    /**
     * Rendering game window frame.
     */
    public void renderWindow() {
        //Adjusting frame's settings
        setSize(WINDOW_SIZE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLocation(INITIAL_WINDOW_POSITION.width/2-this.getSize().width/2, INITIAL_WINDOW_POSITION.height/2-this.getSize().height/2);
        setLayout(new BorderLayout());
        logger.log(Level.INFO, "Game window has been rendered");
    }

    /**
     * Determining game start based on mode the user has selected.
     */
    private void determineGameStart(){
        //User has selected to move pieces before game
        if (movePiecesBeforeGame){
            this.gameModel.setGameState(GameStates.PREGAMEMOVE);
            startGameAfterMovingPieces = new JButton("START GAME");
            startGameAfterMovingPieces.setLayout(null);
            startGameAfterMovingPieces.setBounds(50, 50, 50, 10);
            startGameAfterMovingPieces.setVisible(true);
            startGameAfterMovingPieces.setSize(50, 50);
            this.add(startGameAfterMovingPieces, BorderLayout.NORTH);

            //Iterating over the board to adjust its occupation
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++){
                    Squares square = gameModel.getBoard().getBoardArray()[j][i];
                    square.setCurrentPieceOnSquare(null);
                    square.setOccupation(false);
                }
            }

            GuiMove.guiMove(squarePanel, gameModel);



            startGameAfterMovingPieces.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    //Checking whether the board has necessary pieces
                    if (!areKingsOnBoard() || !areOtherPiecesOnBoard()){
                        JOptionPane.showMessageDialog(e.getComponent(), "You have to place kings and at least one other piece of each color",
                                "Board error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    //User has clicked to start the game
                    else if (e.getSource().equals(startGameAfterMovingPieces)){
                        gameWindow.remove((JButton)e.getSource());
                        gameWindow.revalidate();
                        gameWindow.repaint();
                        gameModel.setGameState(GameStates.ACTIVE);
                        renderPlayerStatusBars();
                        gameModel.getGameController().startGame();
                        logger.log(Level.INFO, "User has ended moving pieces before game and started chess game");
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) { }

                @Override
                public void mouseReleased(MouseEvent e) { }

                @Override
                public void mouseEntered(MouseEvent e) { }

                @Override
                public void mouseExited(MouseEvent e) { }
            });
        }else {
            renderPlayerStatusBars();
            gameModel.getGameController().startGame();
            logger.log(Level.INFO, "Chess game has started");
        }
    }

    /**
     * Rendering status bars of the players.
     */
    private void renderPlayerStatusBars(){
        this.gameWindowPanel = new GameWindowPanel(gameModel, this);
        MenuBar menuBar = new MenuBar(gameModel, this);
        JPanel tmpPanel = new JPanel();

        tmpPanel.setLayout(new BorderLayout());
        tmpPanel.add(menuBar, BorderLayout.WEST);

        if (gameModel.getAbstractPlayer2().getPlayerType().equals(PlayerType.HUMANPLAYER)){
            tmpPanel.add(gameModel.getBlackTimer(), BorderLayout.CENTER);
        }

        add(gameModel.getWhiteTimer(), BorderLayout.SOUTH);
        add(this.gameWindowPanel);
        add(tmpPanel, BorderLayout.NORTH);

        this.revalidate();
        this.repaint();
    }

    /**
     * Check kings presence on board.
     * @return indicator whether kings of both colors are on board
     */
    private boolean areKingsOnBoard(){
        boolean retVal = false;
        boolean blackKingPresence = false;
        boolean whiteKingPresence = false;

        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                Squares square = gameModel.getBoard().getBoardArray()[j][i];

                if (!square.isTaken()){
                    continue;
                }

                MasterAbstractPiece pieceOnSquare = square.getCurrentPieceOnSquare();
                PieceColor pieceOnSquareColor = pieceOnSquare.getColor();

                if (pieceOnSquare.getPieceType().equals(PieaceType.KING) && pieceOnSquareColor.equals(PieceColor.WHITE)){
                    whiteKingPresence = true;
                }

                else if (pieceOnSquare.getPieceType().equals(PieaceType.KING) && pieceOnSquareColor.equals(PieceColor.BLACK)){
                    blackKingPresence = true;
                }
            }
        }
        retVal = blackKingPresence && whiteKingPresence;
        return  retVal;
    }

    /**
     * Checking whether other pieces of both colors are on board.
     * @return
     */
    private boolean areOtherPiecesOnBoard(){
        boolean retVal = false;
        boolean blackPiecePresence = false;
        boolean whitePiecePresence = false;

        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                Squares square = gameModel.getBoard().getBoardArray()[j][i];

                if (!square.isTaken()){
                    continue;
                }

                MasterAbstractPiece pieceOnSquare = square.getCurrentPieceOnSquare();
                PieceColor pieceOnSquareColor = pieceOnSquare.getColor();

                if (!pieceOnSquare.getPieceType().equals(PieaceType.KING) && pieceOnSquareColor.equals(PieceColor.WHITE)){
                    whitePiecePresence = true;
                }

                else if (!pieceOnSquare.getPieceType().equals(PieaceType.KING) && pieceOnSquareColor.equals(PieceColor.BLACK)){
                    blackPiecePresence = true;
                }
            }
        }
        retVal = blackPiecePresence && whitePiecePresence;
        return  retVal;
    }
}
