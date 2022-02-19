package company.Game;

import company.Pieces.PieceColor;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which represents chess time for each player.
 */
public class ChessTimer extends JLabel implements Runnable{
    private long minutes = 30;
    private long initSeconds = minutes * 60;
    private GameModel gameModel;
    private long startTime;
    private long totalSecondsLeft;
    private long moveSeconds;
    private long previousElapsedTime;
    private boolean isActive;
    private final transient Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor of ChessTimer class.
     * @param gameModel instance of the gameModel
     */
    public ChessTimer(GameModel gameModel){
        this.gameModel = gameModel;
        this.isActive = false;

        this.setText(this.minutes + ":0");
        this.startTime = System.currentTimeMillis();
    }


    /**
     * Run method which handles chess timer which runs on separate thread.
     */
    @Override
    public void run() {
        //If the the active status is true, run the timer
        while (isActive) {
            try {
                //Computing elapsed time and time to display in gui window
                long currentElapsedTime = System.currentTimeMillis() - startTime;
                this.moveSeconds = currentElapsedTime / 1000;
                this.totalSecondsLeft = this.initSeconds - this.moveSeconds;
                long minutesLeft = totalSecondsLeft / 60;
                long secondsLeft = totalSecondsLeft % 60;

                //Updating time in GUI
                this.setText(minutesLeft + ":" + secondsLeft);
            } catch (Exception e) {
                logger.log(Level.WARNING, e.toString());
            }
        }

        //In case the player runs out of time, end the game appropriately
        if (this.initSeconds == 0){
            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                gameModel.setGameState(GameStates.BLACK_WIN);
            }else {
                gameModel.setGameState(GameStates.WHITE_WIN);
            }
            gameModel.getGameController().endGame();
        }
    }

    /**
     * Method to resume to timer.
     */
    public void resume(){
        synchronized (this){
            this.startTime = System.currentTimeMillis();
            this.isActive = true;
        }
    }

    /**
     * Method to stop the timer.
     */
    public void stop(){
        synchronized (this){
            this.initSeconds -= this.moveSeconds;
            this.isActive = false;
        }
    }

    /**
     * Getter whether timer is active or not.
     * @return boolean whether timer is active or not
     */
    public boolean isActive() {
        return isActive;
    }
}