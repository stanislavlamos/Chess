package company.View;

import company.Board.Board;
import company.Game.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing settings window before game.
 */
public class GameStartSettings extends JFrame{
    private GameModel gameModel;
    private Dimension PREGAME_WINDOW_SIZE = new Dimension(300, 300);
    private Dimension BUTTON_PANEL_SIZE = new Dimension(300, 50);
    private final Dimension INITIAL_WINDOW_POSITION = Toolkit.getDefaultToolkit().getScreenSize();
    private JRadioButton movePieceBeforeGame;
    private JRadioButton loadMovesFromFile;
    private JRadioButton loadPNGFile;
    private JRadioButton startNormalDirectly;
    private ButtonGroup buttonGroup;
    private JButton startGameButton;
    private JButton backStartMenu;
    private JPanel panelWithButtons;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor of the Game Start settings class.
     * @param gameModel instance of the gameModel
     */
    public GameStartSettings(GameModel gameModel){
        this.gameModel = gameModel;
        this.buttonGroup = new ButtonGroup();

        renderJRadioButtons();
        renderButtons();
        renderFrame();
        this.add(this.startNormalDirectly);
        this.add(this.movePieceBeforeGame);
        this.add(this.loadPNGFile);
        this.add(this.loadMovesFromFile);
        this.add(panelWithButtons);

        validate();
        this.repaint();
    }

    /**
     * Method to render the frame.
     */
    private void renderFrame(){
        //Adjusting frame settings
        logger.log(Level.INFO, "Game settings window has been loaded");
        setTitle("Pregame settings");
        setSize(PREGAME_WINDOW_SIZE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLocation(INITIAL_WINDOW_POSITION.width/2-this.getSize().width/2, INITIAL_WINDOW_POSITION.height/2-this.getSize().height/2);
        setLayout(new GridLayout(5, 1));

    }

    //Adding radio buttons to frame
    private void renderJRadioButtons(){
        //Creating radio buttons
        this.loadPNGFile = new JRadioButton("Load moves from PGN file", false);
        this.loadMovesFromFile = new JRadioButton("Load moves from file", false);
        this.movePieceBeforeGame = new JRadioButton("Move Pieces before game", false);
        this.startNormalDirectly = new JRadioButton("Start normal game", false);

        this.loadMovesFromFile.setVisible(true);
        this.loadPNGFile.setVisible(true);
        this.movePieceBeforeGame.setVisible(true);
        this.startNormalDirectly.setVisible(true);

        //Adding radio buttons to buttonGroup
        this.buttonGroup.add(movePieceBeforeGame);
        this.buttonGroup.add(startNormalDirectly);
        this.buttonGroup.add(loadMovesFromFile);
        this.buttonGroup.add(loadPNGFile);
    }

    /**
     * Rendering buttons to the frame.
     */
    private void renderButtons(){
        //Creating buttons
        this.startGameButton = new JButton("Start game");
        startGameButton.setLayout(new GridBagLayout());
        startGameButton.addMouseListener(new buttonMouseListener(this));

        this.backStartMenu = new JButton("Back to main menu");
        backStartMenu.setLayout(new GridBagLayout());
        backStartMenu.addMouseListener(new buttonMouseListener(this));

        this.panelWithButtons = new JPanel(new GridLayout(1, 2));
        panelWithButtons.setPreferredSize(BUTTON_PANEL_SIZE);

        //Adding buttons to panel
        panelWithButtons.add(startGameButton);
        panelWithButtons.add(backStartMenu);
    }

    /**
     * Inner class which represents implementation of MouseListener.
     */
    protected class buttonMouseListener implements MouseListener{
        private final JFrame gameStartSettings;
        private final String defaultMessage = "\nYou can move pieces with left mouse click and reverse your selection with right mouse click.\n" +
                "Player with white pieces starts.";

        /**
         * Constructor of the buttonMouseListener class.
         * @param gameStartSettings instance of the settings window
         */
        public buttonMouseListener(JFrame gameStartSettings){
            this.gameStartSettings = gameStartSettings;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getComponent().equals(startGameButton)){
                RadioButtonsOutput radioButtonsOutput = determineRadioButtonsOutput();

                //User chose to move pieces before start of the game
                if (radioButtonsOutput.equals(RadioButtonsOutput.MOVE_PIECES_BEFORE_GAME)){
                    String message = "You are about to start a chess game with an option to move pieces at the beginning. " + defaultMessage;
                    Integer choice = JOptionPane.showOptionDialog(gameStartSettings, message, "Game Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                    if (choice.equals(JOptionPane.OK_OPTION)){
                        WindowView windowView = new WindowView(gameModel, true);
                        gameStartSettings.dispose();
                        logger.log(Level.INFO, "User chose to move pieces before the start of the game");
                    }

                }

                //User chose to load moves from file
                else if (radioButtonsOutput.equals(RadioButtonsOutput.LOAD_MOVES_FILE)){
                    String filePath = getPathFromFileMenu();
                    System.out.println(filePath);

                    if(filePath == null){
                        return;
                    }

                    if (!gameModel.getFileReader().readFile(gameModel, filePath)){
                        JOptionPane.showMessageDialog(gameStartSettings, "File error. Try again or check file with moves.",
                                "File Error",
                                JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.SEVERE, "Problem with moves file has occurred");
                        return;
                    }

                    String message = "You are about to load board from file and start chess game. " + defaultMessage;
                    Integer choice = JOptionPane.showOptionDialog(gameStartSettings, message, "Game Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (choice.equals(JOptionPane.OK_OPTION)){
                        WindowView windowView = new WindowView(gameModel, false);
                        gameStartSettings.dispose();
                        logger.log(Level.INFO, "User chose to load moves from file before start of the game");
                    }else{
                        Board board = new Board();
                        gameModel.setBoard(board);
                    }
                }

                //User chose to play direct game
                else if (radioButtonsOutput.equals(RadioButtonsOutput.DIRECT_GAME)){
                    String message = "You are about to start a direct chess game. " + defaultMessage;
                    Integer choice = JOptionPane.showOptionDialog(gameStartSettings, message, "Game Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (choice.equals(JOptionPane.OK_OPTION)){
                        WindowView windowView = new WindowView(gameModel, false);
                        gameStartSettings.dispose();
                        logger.log(Level.INFO, "User chose to play direct chess game");
                    }
                }

                //User chose to load moves from PGN
                else if (radioButtonsOutput.equals(RadioButtonsOutput.LOAD_PNG)){
                    String filePath = getPathFromFileMenu();

                    if(filePath == null){
                        return;
                    }

                    if (!gameModel.getPgnReader().readPgnFile(filePath)){
                        JOptionPane.showMessageDialog(gameStartSettings, "File error. Try again or check file with moves.",
                                "File Error",
                                JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.SEVERE, "Problem with PGN file has occurred");
                        return;
                    }

                    //Checking whether you loaded finished game
                    if (gameModel.getGameState() != null){
                        JOptionPane.showMessageDialog(gameStartSettings, "You have loaded finished game. Try different PGN file!",
                                "Finished game",
                                JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.WARNING, "You have loaded PGN file with finished game.");

                    }else {
                        String message = "You are about to load moves from PGN file and start a chess. " + defaultMessage;
                        Integer choice = JOptionPane.showOptionDialog(gameStartSettings, message, "Game Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                        if (choice.equals(JOptionPane.OK_OPTION)){
                            WindowView windowView = new WindowView(gameModel, false);
                            gameStartSettings.dispose();
                            logger.log(Level.INFO, "User has loaded moves from PGN file");
                        }else {
                            Board board = new Board();
                            gameModel.setBoard(board);
                        }
                    }
                }

            }else{
                new MainMenuWindow();
                gameStartSettings.dispose();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        /**
         * Method to determine on which radio button has user clicked.
         * @return  radio button on which has user clicked
         */
        private RadioButtonsOutput determineRadioButtonsOutput(){
            RadioButtonsOutput retval;

            if (movePieceBeforeGame.isSelected()){
                retval = RadioButtonsOutput.MOVE_PIECES_BEFORE_GAME;
            }

            else if (loadMovesFromFile.isSelected()){
                retval = RadioButtonsOutput.LOAD_MOVES_FILE;
            }

            else if (startNormalDirectly.isSelected()){
                retval = RadioButtonsOutput.DIRECT_GAME;
            }

            else if (loadPNGFile.isSelected()){
                retval = RadioButtonsOutput.LOAD_PNG;
            }

            else {
                retval = RadioButtonsOutput.NOT_SELECTED;
            }
            return retval;
        }

        /**
         * Method to display file chooser menu when user wants to load moves from file.
         * @return  path to file
         */
        private String getPathFromFileMenu(){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to load moves");
            int result = fileChooser.showOpenDialog(this.gameStartSettings);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                return selectedFile.getAbsolutePath();
            }
            return null;
        }

    }

    /**
     * Enum representing the output of each radio button on the screen.
     */
    private enum RadioButtonsOutput{
        MOVE_PIECES_BEFORE_GAME,
        LOAD_PNG,
        LOAD_MOVES_FILE,
        DIRECT_GAME,
        NOT_SELECTED
    }
}
