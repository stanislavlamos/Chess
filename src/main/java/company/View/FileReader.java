package company.View;

import company.Board.Board;
import company.Game.GameModel;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Method to read and write files to store the game.
 */
public class FileReader {
    private static FileReader single_instance = null;
    private static final transient Logger logger = Logger.getLogger(FileReader.class.getName());

    /**
     * Private constructor of the File reader class.
     */
    private FileReader(){}

    /**
     * Method to get single instance of the class.
     * @return  instance of the class
     */
    public static FileReader getInstance(){
        if (single_instance == null){
            single_instance = new FileReader();
        }
        return single_instance;
    }

    /**
     * Method to read moves from file.
     * @param gameModel instance of the gameModel
     * @param filePath  path of the file
     * @return  indicator whether the reading was successful or not
     */
    public boolean readFile(GameModel gameModel, String filePath){
        boolean retVal = true;
        //Opening the file and updating the board
        try {
            FileInputStream fi = new FileInputStream(new File(filePath));
            ObjectInputStream oi = new ObjectInputStream(fi);

            Board pr1 = (Board) oi.readObject();
            gameModel.setBoard(pr1);

            oi.close();
            fi.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
            retVal = false;
            e.printStackTrace();
        }
        return retVal;
    }

    /**
     * Method to write board object to file.
     * @param gameModel instance of the gameModel
     * @param filePath  path to file
     * @return  indicator whether the writing was successful or not
     */
    public boolean writeFile(GameModel gameModel, String filePath){
        boolean retVal = true;
        //Opening the file and writing board object
        try{
            FileOutputStream f = new FileOutputStream(new File(filePath));
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(gameModel.getBoard());

            o.close();
            f.close();
        }catch (Exception e){
            logger.log(Level.WARNING, e.toString());
            retVal = false;
        }
        return retVal;
    }
}
