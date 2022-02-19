package company.View;

import company.Game.GameModel;
import company.Game.GameStates;
import company.Game.PgnWriter;
import company.Player.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Class which represents menu bar in game frame.
 */
public class MenuBar extends JMenuBar {
    private GameModel gameModel;
    private WindowView windowView;
    private JMenu actionMenu = new JMenu("Game actions");

    private JMenuItem i1 =new JMenuItem("Save moves");
    private JMenuItem i2 =new JMenuItem("Save moves to PGN");
    private JMenuItem i3 =new JMenuItem("Draw and quit");
    private JMenuItem i4 =new JMenuItem("Quit game in progress");

    /**
     * Constructor of the game frame.
     * @param gameModel instance of the gameModel
     * @param windowView    instance of the windowView frame
     */
    public MenuBar(GameModel gameModel, WindowView windowView){
        this.gameModel = gameModel;
        this.windowView = windowView;

        render();
    }

    /**
     * Method to add and render menu bar and its items on screen.
     */
    private void render(){
        setBackground(Color.PINK);

        i1.addActionListener(new ActionListenerImpl());
        i2.addActionListener(new ActionListenerImpl());
        i3.addActionListener(new ActionListenerImpl());
        i4.addActionListener(new ActionListenerImpl());

        actionMenu.add(i1);
        actionMenu.add(i2);

        if (!gameModel.getAbstractPlayer1().getPlayerType().equals(PlayerType.COMPUTERPLAYER)
                && !gameModel.getAbstractPlayer2().getPlayerType().equals(PlayerType.COMPUTERPLAYER)){
            actionMenu.add(i3);
        }

        actionMenu.add(i4);

        add(actionMenu);
    }

    /**
     * Inner class representing the implementation of ActionListener.
     */
    protected class ActionListenerImpl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            //User chose to save moves to file
            if (e.getSource().equals(i1)){
               gameModel.getGameController().stopChessTimerForPlayerOnturn();
               JFileChooser fileChooser = new JFileChooser();
               fileChooser.setDialogTitle("Specify a file to save your moves");

               Integer userSelection = fileChooser.showSaveDialog(gameModel.getWindowView());

               if (userSelection.equals(JFileChooser.APPROVE_OPTION)){
                   File fileToSave = fileChooser.getSelectedFile();
                   gameModel.getFileReader().writeFile(gameModel, fileToSave.getAbsolutePath());
               }

               gameModel.getGameController().resumeChessTimerForPlayerOnTurn();
            }


            //User chose to save moves to PGN file
            else if (e.getSource().equals(i2)){
                gameModel.getGameController().stopChessTimerForPlayerOnturn();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save your moves");

                Integer userSelection = fileChooser.showSaveDialog(gameModel.getWindowView());

                if (userSelection.equals(JFileChooser.APPROVE_OPTION)){
                    File fileToSave = fileChooser.getSelectedFile();
                    PgnWriter.writePgnNotationToFile(gameModel, fileToSave.getAbsolutePath());
                }

                gameModel.getGameController().resumeChessTimerForPlayerOnTurn();
            }


            //Players agreed to end game in draw
            else if (e.getSource().equals(i3)){
                gameModel.setGameState(GameStates.DRAW);
                gameModel.getGameController().endGame();
            }


            //User chose to leave game in progress
            else if(e.getSource().equals(i4)){
                gameModel.setGameState(GameStates.LEFT_GAME_IN_PROGRESS);
                gameModel.getGameController().endGame();
            }

        }
    }
}
