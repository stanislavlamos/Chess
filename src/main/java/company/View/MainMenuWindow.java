package company.View;

import company.Board.Board;
import company.Game.GameModel;
import company.Pieces.PieceColor;
import company.Player.AbstractPlayer;
import company.Player.ComputerPlayer;
import company.Player.HumanPlayer;
import company.Player.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing the main window.
 */
public class MainMenuWindow extends JFrame {
    private final Dimension MENU_WINDOW_SIZE = new Dimension(300, 300);
    private JButton oneOnOne = new JButton("1v1");
    private JButton oneOnComputer = new JButton("1vComputer");
    private JPanel buttonsPanel;
    private final Dimension INITIAL_WINDOW_POSITION = Toolkit.getDefaultToolkit().getScreenSize();
    private Dimension BUTTON_PANEL_SIZE = new Dimension(300, 50);
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor of the main menu window class.
     */
    public MainMenuWindow(){
        render();
        renderButtons();

        validate();
        this.repaint();
    }

    /**
     * Render the main window frame.
     */
    private void render(){
        //Adjusting the settings of main window frame
        setTitle("Main menu");
        setSize(MENU_WINDOW_SIZE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocation(INITIAL_WINDOW_POSITION.width/2-this.getSize().width/2, INITIAL_WINDOW_POSITION.height/2-this.getSize().height/2);
        setLayout(new BorderLayout());
        setVisible(true);
        logger.log(Level.INFO, "User has started the game");
    }

    /**
     * Rendering buttons.
     */
    private void renderButtons(){
        //Adjusting buttons' settings
        oneOnOne.setLayout(new GridBagLayout());
        oneOnOne.addMouseListener(new mouseListenerImpl(this));

        oneOnComputer.setLayout(new GridBagLayout());
        oneOnComputer.addMouseListener(new mouseListenerImpl(this));

        buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.setPreferredSize(BUTTON_PANEL_SIZE);

        //adding buttons to panel
        buttonsPanel.add(oneOnOne);
        buttonsPanel.add(oneOnComputer);

        //Adding panel with buttons to the frame
        this.add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Inner class which represents implementation of MouseListener.
     */
    protected class mouseListenerImpl implements MouseListener{
        private JFrame menuWindow;

        /**
         * Constructor of mouseListenerImpl class.
         * @param menuWindow    instance of the main menu frame
         */
        public mouseListenerImpl(JFrame menuWindow){
            this.menuWindow = menuWindow;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            GameModel gameModel;
            AbstractPlayer player1;
            AbstractPlayer player2;

            //User chose to play 1v1 game mode
            if (e.getComponent().equals(oneOnOne)){
                player1 = new HumanPlayer("Player1", PieceColor.WHITE, PlayerType.HUMANPLAYER, 30, 0);
                player2 = new HumanPlayer("Player2", PieceColor.BLACK, PlayerType.HUMANPLAYER, 30, 0);
                logger.log(Level.INFO, "User chose to play 1v1 mode");
            }
            else {
                //User chose to play 1vcomputer mode
                player1 = new HumanPlayer("Player1", PieceColor.WHITE, PlayerType.HUMANPLAYER, 30, 0);
                player2 = new ComputerPlayer("Computer Player", PieceColor.BLACK, PlayerType.COMPUTERPLAYER);
                logger.log(Level.INFO, "User chose to play 1vComputer mode");
            }

            //Creating new board and starting game settings window
            Board board = new Board();
            gameModel = new GameModel(player1, player2, board);


            GameStartSettings gameStartSettings = new GameStartSettings(gameModel);
            menuWindow.dispose();
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

}
