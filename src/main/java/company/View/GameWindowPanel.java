package company.View;

import company.Game.GameModel;

import javax.swing.*;
import java.awt.*;

/**
 * Class which stores info panels of both players in JPanel.
 */
public class GameWindowPanel extends JPanel {
    private WindowView windowView;
    private PlayerInfoPanel player1Panel;
    private PlayerInfoPanel player2Panel;
    private GameModel gameModel;

    /**
     * Constructor of the GameWindowPanel class.
     * @param gameModel instance of the gameModel
     * @param windowView instance of the main game window
     */
    public GameWindowPanel(GameModel gameModel, WindowView windowView){
        this.gameModel = gameModel;
        this.windowView = windowView;

        this.player1Panel = new PlayerInfoPanel(gameModel, gameModel.getAbstractPlayer1(), windowView);
        this.player2Panel = new PlayerInfoPanel(gameModel, gameModel.getAbstractPlayer2(), windowView);

        render();
    }

    /**
     * Method to render and add players' info panels to the main game window.
     */
    private void render(){
        setLayout(new BorderLayout());
        add(player1Panel, BorderLayout.SOUTH);
        add(player2Panel, BorderLayout.NORTH);
        setVisible(true);
    }
}
