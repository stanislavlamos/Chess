package company.Game;

import company.Pieces.PieceColor;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to write PGN moves to file.
 */
public class PgnWriter {
    private static final Logger logger = Logger.getLogger(PgnWriter.class.getName());

    /**
     * Private constructor of PGN writer class.
     */
    private PgnWriter(){}

    /**
     * Wirting PGN moves to file.
     * @param gameModel instance of the game model
     * @param path  path to file
     */
    public static void writePgnNotationToFile(GameModel gameModel, String path){
        try{
            FileWriter fileWriter = new FileWriter(path);
            Map<GameStates, String> gameStatesToStringMap = fillgameStatesStringMap();

            String tagRoster = tagRoster(gameModel, gameStatesToStringMap);
            String pgnNotation = getPgnNotation(gameModel);

            fileWriter.write(tagRoster);
            fileWriter.write(System.lineSeparator());
            fileWriter.write(pgnNotation);

            String result = determineResult(gameModel, gameStatesToStringMap);
            if (!result.equals("*")){
                fileWriter.write("  " + result);
            }

            fileWriter.close();

        }catch (Exception e){
            logger.log(Level.WARNING, e.toString());
        }
    }

    /**
     * Writing tag roster to file.
     * @param gameModel instance of the gameModel
     * @param gameStatesToStringMap map to convert game states to string
     * @return  returning final tag roster to write to file
     */
    private static String tagRoster(GameModel gameModel, Map<GameStates, String> gameStatesToStringMap){
        String retVal = "";
        ArrayList<String> tagRoster = gameModel.getBoard().getPgnTagRoster();

        String result = "[Result \"" + determineResult(gameModel, gameStatesToStringMap) + "\"]";

        String white = "";
        String black = "";

        //Handling naming players based on their color
        if (gameModel.getAbstractPlayer1().getPlayerPieceColor().equals(PieceColor.WHITE)){
            white = "[White \"" + gameModel.getAbstractPlayer1().getPlayerName() + "\"]" + System.lineSeparator();
            black = "[Black \"" + gameModel.getAbstractPlayer2().getPlayerName() + "\"]" + System.lineSeparator();
        }else {
            white = "[White \"" + gameModel.getAbstractPlayer2().getPlayerName() + "\"]" + System.lineSeparator();
            black = "[Black \"" + gameModel.getAbstractPlayer1().getPlayerName() + "\"]" + System.lineSeparator();
        }


        //Generating our own tag roster
        if (tagRoster.size() == 0){
            String event = "[Event \"PJV chess contest\"]" + System.lineSeparator();
            String site = "[Site \"Remote\"]" + System.lineSeparator();
            String date = "[Date \"?\"]" + System.lineSeparator();
            String round = "[Round \"?\"]" + System.lineSeparator();

            retVal = event + site + date + round + white + black + result;


        }else{
            //Getting PGN roster from loaded file
            ArrayList<String> pgnRoaster = gameModel.getBoard().getPgnTagRoster();
            pgnRoaster.set(4, white);
            pgnRoaster.set(5, black);
            pgnRoaster.set(6, result);

            Integer curIndex = 0;
            for (String infoTag : pgnRoaster){
                curIndex += 1;

                if (curIndex == gameModel.getBoard().getPgnTagRoster().size()){
                    String tmpRetval = retVal;
                    retVal = tmpRetval + infoTag;
                }else {
                    String tmpRetval = retVal;
                    retVal = tmpRetval + infoTag + System.lineSeparator();
                }
            }
        }
        return retVal;
    }

    /**
     * Filling map of gamestates to string.
     * @return map of gamestates to string
     */
    private static Map<GameStates, String> fillgameStatesStringMap(){
        Map<GameStates, String> retMap = new HashMap<>();

        retMap.put(GameStates.DRAW, "1/2-1/2");
        retMap.put(GameStates.WHITE_WIN, "1-0");
        retMap.put(GameStates.BLACK_WIN, "0-1");
        retMap.put(GameStates.LEFT_GAME_IN_PROGRESS, "*");
        retMap.put(GameStates.ACTIVE, "*");

        return retMap;
    }

    /**
     * Getting PGN notations from gameModel.
     * @param gameModel instance of the gameModel
     * @return string of PGN notations for file
     */
    private static String getPgnNotation(GameModel gameModel){
        String retVal = "";
        Integer index = 0;
        for (Move move : gameModel.getBoard().getMoveHistory()){

            if (index % 6 == 0){
                String tmpRetVal = retVal;
                retVal = tmpRetVal + System.lineSeparator();
            }

            if (index % 2 == 0){
                String pgnNotation = move.getPgnNotation();
                String tmpRetval = retVal;
                retVal = tmpRetval + (index / 2 + 1) + ". " + pgnNotation;
            }

            else {
                String pgnNotation = move.getPgnNotation();
                String tmpRetval = retVal;
                retVal = tmpRetval + " " + pgnNotation + " ";
            }

            index += 1;
        }
        return retVal;
    }

    /**
     * Method to determine PGN result based on game state.
     * @param gameModel
     * @param gameStatesToStringMap
     * @return game result for PGN file
     */
    private static String determineResult (GameModel gameModel, Map<GameStates, String> gameStatesToStringMap){
        String result = "";
        GameStates currentGameState = gameModel.getGameState();

        if (currentGameState.equals(GameStates.CHECKMATE)){
            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                result = gameStatesToStringMap.get(GameStates.BLACK_WIN);
            }else {
                result = gameStatesToStringMap.get(GameStates.WHITE_WIN);
            }
        }

        else if (currentGameState.equals(GameStates.RESIGNATION)){
            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                result = gameStatesToStringMap.get(GameStates.BLACK_WIN);
            }else {
                result = gameStatesToStringMap.get(GameStates.WHITE_WIN);
            }
        }

        else {
            result = gameStatesToStringMap.get(currentGameState);
        }

        return result;
    }
}
