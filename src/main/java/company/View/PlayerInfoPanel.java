package company.View;

import company.Game.GameModel;
import company.Game.GameStates;
import company.Pieces.PieceColor;
import company.Player.AbstractPlayer;
import company.Player.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Class which represents info panel of the player.
 */
public class PlayerInfoPanel extends JPanel {
    private GameModel gameModel;
    private AbstractPlayer player;
    private JButton resign = new JButton("Resign");
    private JLabel playersName;
    private JLabel playersTime;
    private JLabel playersPieceColor;
    private WindowView windowView;
    private String playersPieceColorString;

    /**
     * Constructor of the PlayerInfoPanel class.
     * @param gameModel instance of the gameModel
     * @param player    player to whom this panel belongs to
     * @param windowView    instance of the main game window frame
     */
    public PlayerInfoPanel(GameModel gameModel, AbstractPlayer player, WindowView windowView){
        this.gameModel = gameModel;
        this.player = player;
        this.windowView = windowView;

        this.playersPieceColorString = this.player.getPlayerPieceColor().equals(PieceColor.WHITE) ? "WHITE" : "BLACK";

        this.playersName = new JLabel(player.getPlayerName());
        this.playersPieceColor = new JLabel(playersPieceColorString + " Pieces");

        render();
    }

    /**
     * Method for rendering the panel.
     */
    private void render(){
        setLayout(new GridLayout(4,1));
        add(playersName);
        add(playersPieceColor);

        //Checking whether the player is Human player
        if(player.getPlayerType().equals(PlayerType.HUMANPLAYER)){
            resign.addMouseListener(new MouseListenerImpl());
            add(resign);
        }
        setVisible(true);

    }

    /**
     * Inner which holds the implementation of the MouseListener.
     */
    protected class MouseListenerImpl implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            //Handling player's resignation
            if (gameModel.getPlayerOnTurn().equals(player)){
                gameModel.setGameState(GameStates.RESIGNATION);
                gameModel.getGameController().endGame();
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
    }


}
