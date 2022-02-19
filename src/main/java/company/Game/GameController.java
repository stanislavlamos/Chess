package company.Game;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Board.Files;
import company.Board.Squares;
import company.Pieces.*;
import company.Player.AbstractPlayer;
import company.Player.PlayerType;
import company.View.GuiMove;
import company.View.MainMenuWindow;
import company.View.PawnPromotionWindow;
import company.View.SquarePanel;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Class controlling the game of chess.
 */
public class GameController {
    private GameModel gameModel;
    private SquarePanel squarePanel;
    private PawnPromotionWindow pawnPromotionWindow;
    private Map<Files, String> fileStringMap;
    private final Logger logger = Logger.getLogger(this.getClass().getName());


    /*
     * Constructor of GameController class attributes.
     * @param gameModel   Model of the holding its attributes
     */
    public GameController(GameModel gameModel){
        this.gameModel = gameModel;

        fillFileStringMap();
    }

    /*
     * Method starting the game of chess.
     */
    public void startGame(){
        AbstractPlayer[] playerArr = new AbstractPlayer[2];
        AbstractPlayer playerOnTurn;
        GameStates gameState;

        playerArr[0] = gameModel.getAbstractPlayer1();
        playerArr[1] = gameModel.getAbstractPlayer2();


        for (AbstractPlayer player : playerArr){
            if(player.getPlayerPieceColor().equals(PieceColor.BLACK)){
                gameModel.setPlayerOnTurn(player);
            }
        }

        gameState = GameStates.ACTIVE;
        gameModel.setPlayerArr(playerArr);
        gameModel.setGameState(gameState);
        gameModel.changePlayerOnTurn();
    }

    /*
     * Method for initiating move.
     * @param player    player making the move
     * @param from  starting location of the move process
     * @param to    final location of the move
     * @param squarePanel   Jpanel holding the chess squares in GUI
     */
    public void initiateMove(AbstractPlayer player, BoardLocation from, BoardLocation to, SquarePanel squarePanel){
        String pgnNotation = generatePgnNotation(from, to);
        MasterAbstractPiece movingPiece = gameModel.getBoard().getLocationSquaresMap().get(from).getCurrentPieceOnSquare();
        Move newMove = new Move(player, from, to, movingPiece, pgnNotation);
        this.squarePanel = squarePanel;

        //Checking whether them move is valid
        if (moveLogic(newMove)){
            updateBoard(from, to);

            logger.log(Level.INFO, gameModel.getPlayerOnTurn().getPlayerName()
                    + "has moved from " + from.toString() + " to " + to.toString());

            gameModel.getBoard().addMoveHistory(newMove);
            GuiMove.guiMove(player, from, to, squarePanel, gameModel);

            //Checking whether the pawn promotion has occured
            if (checkPawnPromotion() && gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.HUMANPLAYER)) {
                pawnPromotionWindow = new PawnPromotionWindow(gameModel, squarePanel.getBoardTileArray()[to.getFile().ordinal()][to.getRank() - 1]);
            }else {
                gameModel.changePlayerOnTurn();
            }
        }
    }


    /*
     * Method for initiating move used by computer player.
     * @param player    player making the move
     * @param from  starting location of the move process
     * @param to    final location of the move
     */
    public void initiateMove(AbstractPlayer player, BoardLocation from, BoardLocation to){
        String pgnNotation = generatePgnNotation(from, to);
        MasterAbstractPiece movingPiece = gameModel.getBoard().getLocationSquaresMap().get(from).getCurrentPieceOnSquare();
        Move newMove = new Move(player, from, to, movingPiece, pgnNotation);

        //Checking whether them move is valid
        if (moveLogic(newMove)){
            gameModel.getBoard().addMoveHistory(newMove);
            updateBoard(from, to);

            logger.log(Level.INFO, gameModel.getPlayerOnTurn().getPlayerName()
                    + "has moved from " + from.toString() + " to " + to.toString());

            GuiMove.guiMove(player, from, to, this.squarePanel, gameModel);
            checkPawnPromotion();

            //Checking whether the pawn promotion has occured
            //If so, promote the pawn to queen
            if (checkPawnPromotion() && gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.COMPUTERPLAYER)){
                gameModel.getBoard().handlePawnPromotionPgn("Q");
                Squares toSquare = gameModel.getBoard().getLocationSquaresMap().get(to);
                toSquare.setCurrentPieceOnSquare(new Queen(PieceColor.BLACK));
                updateBoard(to, to);
                GuiMove.guiMove(player, from, to, this.squarePanel, gameModel);
                gameModel.setPawnPromotion(false);
            }
            gameModel.setPawnPromotion(false);
            gameModel.changePlayerOnTurn();
        }
    }


    /*
     * Method for checking whether the desired move passed the game logic.
     * @param newMove   player's desired move
     * @return  boolean indicating whether the move passed the game logic or not
     */
    private boolean moveLogic(Move newMove){
        boolean retVal = false;
        AbstractPlayer playerOnMove = newMove.getPlayer();
        MasterAbstractPiece movingPiece = newMove.getMovingPiece();
        BoardLocation to = newMove.getToLocation();
        Board board = gameModel.getBoard();

        //Checking whether the move is valid
        if (movingPiece.checkMoveValidity(to, movingPiece.getValidMoves(board)) && board.getLocationSquaresMap().containsKey(to)){
            retVal = true;

            //If the piece on the move is pawn, change its firstMove indicator to false
            if (movingPiece.getName().equals("Pawn")){
                Pawn pawn = (Pawn) movingPiece;

                if (pawn.isFirstMove()){
                    pawn.setFirstMove(false);
                }

                //If the destination square corresponds with enPassant, make appropriate changes on the board
                if (pawn.isEnPassant() && to.equals(pawn.getEntPassantLocation())){

                    //Making board changes for enPassant move for player with white pieces
                    if (movingPiece.getColor().equals(PieceColor.WHITE)){
                        BoardLocation locationToChange = LocationUtils.createBoardLocation(to, 0, -1);
                        Squares squareToChange = board.getLocationSquaresMap().get(locationToChange);
                        squareToChange.setCurrentPieceOnSquare(null);
                        squareToChange.resetSquareOccupation();
                    }

                    //Making board changes for enPassant move for player with black pieces
                    else if(movingPiece.getColor().equals(PieceColor.BLACK)){
                        BoardLocation locationToChange = LocationUtils.createBoardLocation(to, 0, 1);
                        Squares squareToChange = board.getLocationSquaresMap().get(locationToChange);
                        squareToChange.setCurrentPieceOnSquare(null);
                        squareToChange.resetSquareOccupation();
                    }
                    pawn.setEnPassant(false);
                    pawn.setEntPassantLocation(null);
                }
            }
        }
        return retVal;
    }

    /*
     * Method for updating the state of the chess board.
     * @param from  starting location of the move process
     * @param to    final location of the move process
     */
    public void updateBoard(BoardLocation from, BoardLocation to){
        Squares fromSquare = gameModel.getBoard().getLocationSquaresMap().get(from);
        Squares toSquare = gameModel.getBoard().getLocationSquaresMap().get(to);
        MasterAbstractPiece movingPiece = fromSquare.getCurrentPieceOnSquare();

        //Updating source location
        fromSquare.setCurrentPieceOnSquare(null);
        fromSquare.resetSquareOccupation();

        //Updating destination location
        toSquare.setCurrentPieceOnSquare(movingPiece);
        toSquare.setOccupation(true);
        movingPiece.setSquares(toSquare);
    }

    /**
     * Method to check whther the pawn promotion is possible.
     * @return  boolean indicator whether the pawn promotion is possible
     */
    private boolean checkPawnPromotion(){
        Map<BoardLocation, Squares> locationSquaresMap = gameModel.getBoard().getLocationSquaresMap();

        //Checking first and last rows on the chess board
        for (Integer i = 0; i < 8; i++){
            MasterAbstractPiece whiteCandidate = locationSquaresMap.get(new BoardLocation(Files.values()[i], 8)).getCurrentPieceOnSquare();
            MasterAbstractPiece blackCandidate = locationSquaresMap.get(new BoardLocation(Files.values()[i], 1)).getCurrentPieceOnSquare();

            //Checking whether pawn promotion for player with white pieces is possible
            if (whiteCandidate != null &&
                    whiteCandidate.getPieceType().equals(PieaceType.PAWN)
                    && whiteCandidate.getColor().equals(PieceColor.WHITE)){
                gameModel.setPawnPromotion(true);
                return true;
            }

            //Checking whether pawn promotion for player with white pieces is possible
            else if (blackCandidate != null && blackCandidate.getPieceType().equals(PieaceType.PAWN)
                    && blackCandidate.getColor().equals(PieceColor.BLACK)){
                gameModel.setPawnPromotion(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Method for ending the game.
     */
    public void endGame(){
        gameModel.killAllThreads();
        String message = "";

        //Handling resignation type of game end
        if (gameModel.getGameState().equals(GameStates.RESIGNATION)){
            String winnerName = gameModel.getPlayerOnTurn().equals(gameModel.getAbstractPlayer1())
                    ? gameModel.getAbstractPlayer2().getPlayerName() : gameModel.getAbstractPlayer1().getPlayerName();

            String loserName = gameModel.getPlayerOnTurn().equals(gameModel.getAbstractPlayer1())
                    ? gameModel.getAbstractPlayer1().getPlayerName() : gameModel.getAbstractPlayer2().getPlayerName();

            message = winnerName + " has won. " + loserName + " has resignated.";
            logger.log(Level.INFO, "Game has ended, " + loserName + " has resignated");
        }

        //Handling draw of chess game
        else if(gameModel.getGameState().equals(GameStates.DRAW)){
            message = "The game resulted in draw";
            logger.log(Level.INFO, "Game has resulted in draw");
        }

        //Handling when checkmate has occurred
        else if (gameModel.getGameState().equals(GameStates.CHECKMATE)){
            String loserName = gameModel.getPlayerOnTurn().getPlayerName();

            String winnerName = gameModel.getPlayerOnTurn().equals(gameModel.getAbstractPlayer1())
                    ? gameModel.getAbstractPlayer2().getPlayerName() : gameModel.getAbstractPlayer1().getPlayerName();

            message = winnerName + " has won. " + loserName + " has been checkmated.";
            logger.log(Level.INFO, "Game has ended, " + loserName + " has been checkmated");
        }

        //Handling when the player with black pieces has won
        else if (gameModel.getGameState().equals(GameStates.BLACK_WIN)){
            String winnerName = gameModel.getAbstractPlayer1().getPlayerPieceColor().equals(PieceColor.BLACK)
                    ? gameModel.getAbstractPlayer1().getPlayerName() : gameModel.getAbstractPlayer2().getPlayerName();

            message = winnerName + " has won.";
            logger.log(Level.INFO, "Game has ended, " + winnerName  + " has won.");
        }

        //Handling when the player with white pieces has won
        else if (gameModel.getGameState().equals(GameStates.WHITE_WIN)){
            String winnerName = gameModel.getAbstractPlayer1().getPlayerPieceColor().equals(PieceColor.WHITE)
                    ? gameModel.getAbstractPlayer1().getPlayerName() : gameModel.getAbstractPlayer2().getPlayerName();
            message = winnerName + " has won.";
            logger.log(Level.INFO, "Game has ended, " + winnerName  + " has won.");
        }

        //Handling when the user has left the game in progress
        else if(gameModel.getGameState().equals(GameStates.LEFT_GAME_IN_PROGRESS)){
            message = "You have left the game in progress";
            logger.log(Level.INFO, "Player has left the gamer in progress");
        }

        //Displaying information window
        JOptionPane.showMessageDialog(gameModel.getWindowView(), message,
                "Game ended",
                JOptionPane.INFORMATION_MESSAGE);

        String[] saveOptions = {"Save moves to PNG", "Cancel"};
        Integer choice = JOptionPane.showOptionDialog(gameModel.getWindowView(), "Do you want to save your moves to file?",
                "Save actions before end", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, saveOptions, null);

        //Displaying file chooser if user wants to save its moves to PGN
        if (choice.equals(JOptionPane.OK_OPTION)){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify location to save your PGN file");

            Integer userSelection = fileChooser.showSaveDialog(gameModel.getWindowView());

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                PgnWriter.writePgnNotationToFile(gameModel, fileToSave.getAbsolutePath());
            }
        }

        new MainMenuWindow();
        gameModel.getWindowView().dispose();
    }

    /**
     * Stopping chess timer for player on turn.
     */
    public void stopChessTimerForPlayerOnturn(){
        if (!gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.COMPUTERPLAYER)){
            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                gameModel.getWhiteTimer().stop();
            }else {
                gameModel.getBlackTimer().stop();
            }
        }
    }

    /**
     * Resume chess timer for player on turn.
     */
    public void resumeChessTimerForPlayerOnTurn(){
        if (gameModel.getPlayerOnTurn().getPlayerType().equals(PlayerType.HUMANPLAYER)){
            if (gameModel.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                gameModel.getWhiteTimer().resume();
                Thread tmpThreadWhite = new Thread(gameModel.getWhiteTimer());
                tmpThreadWhite.start();
            }else {
                gameModel.getBlackTimer().resume();
                Thread tmpThreadBlack = new Thread(gameModel.getBlackTimer());
                tmpThreadBlack.start();
            }
        }
    }

    /**
     * Getter for square panel holding the GUI squares of chess board.
     * @return  SquarePanel current instance of squarePanel
     */
    public SquarePanel getSquarePanel() {
        return squarePanel;
    }

    /**
     * Generating PGN notation for the current move.
     * @param from source location
     * @param to    destination location
     * @return  String PGN notation of the move
     */
    private String generatePgnNotation(BoardLocation from, BoardLocation to){
        Squares toSquare = gameModel.getBoard().getLocationSquaresMap().get(to);
        String pgnNotation = "";
        MasterAbstractPiece movingPiece = gameModel.getBoard().getLocationSquaresMap().get(from).getCurrentPieceOnSquare();

        //In case of Pawn, omit the piece tag
        if (!movingPiece.getPieceType().equals(PieaceType.PAWN)){
            pgnNotation = movingPiece.getPieceTag();
        }

        //If the piece on the move is not panw, add piece tag to PGN notation
        if (movingPiece.getPieceType().equals(PieaceType.PAWN)){
            Pawn pawn = (Pawn) movingPiece;

            //If the square is taken, add 'x' to PGN notation
            if (toSquare.isTaken() || (pawn.isEnPassant() && to.equals(pawn.getEntPassantLocation()))){
                pgnNotation = this.fileStringMap.get(from.getFile()) + "x";
            }

        }

        //Handling move of the piece other than pawn
        else if (!movingPiece.getPieceType().equals(PieaceType.PAWN)){
            String fromNotation = detectAmbiguity(from, to);

            if (toSquare.isTaken()){
                String fromNotationTmp = fromNotation;
                fromNotation= fromNotationTmp + "x";
            }
            String pgnNotationTmp = pgnNotation;
            pgnNotation = pgnNotationTmp + fromNotation;
        }

        String pgnTmp = pgnNotation;
        pgnNotation = pgnTmp + this.fileStringMap.get(to.getFile()) + String.valueOf(to.getRank());

        return pgnNotation;
    }

    /**
     * Method to fill the map of file locations and its PGN notations.
     */
    private void fillFileStringMap(){
        this.fileStringMap = new HashMap<>();

        this.fileStringMap.put(Files.A, "a");
        this.fileStringMap.put(Files.B, "b");
        this.fileStringMap.put(Files.C, "c");
        this.fileStringMap.put(Files.D, "d");
        this.fileStringMap.put(Files.E, "e");
        this.fileStringMap.put(Files.F, "f");
        this.fileStringMap.put(Files.G, "g");
        this.fileStringMap.put(Files.H, "h");
    }

    /**
     * Detecting ambiguity for PGN notation.
     * @param from  source location
     * @param to    destination location
     * @return  String  returning the PGN notation of source location
     */
    private String detectAmbiguity(BoardLocation from, BoardLocation to) {
        String retVal = "";
        PieaceType movingPieceType = gameModel.getBoard().getLocationSquaresMap().get(from).getCurrentPieceOnSquare().getPieceType();
        MasterAbstractPiece movingPiece = gameModel.getBoard().getLocationSquaresMap().get(from).getCurrentPieceOnSquare();

        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (!gameModel.getBoard().getBoardArray()[j][i].isTaken()
                        || gameModel.getBoard().getBoardArray()[j][i].getLocation().equals(from)
                        || gameModel.getBoard().getBoardArray()[j][i].getLocation().equals(to)) {
                    continue;
                }

                Squares currentSquare = gameModel.getBoard().getBoardArray()[j][i];
                MasterAbstractPiece pieceOnSquare = currentSquare.getCurrentPieceOnSquare();
                List<BoardLocation> moveCandidates = pieceOnSquare.getValidMoves(gameModel.getBoard());

                //Detecting whether some other piece of same type and color has destination location in its legal moves array

                if (!pieceOnSquare.getPieceType().equals(movingPieceType) || !moveCandidates.contains(to)){
                    continue;
                }

                if (!currentSquare.getLocation().getFile().equals(from.getFile())){
                    retVal = this.fileStringMap.get(from.getFile());
                }

                else if (!currentSquare.getLocation().getRank().equals(from.getRank())){
                    retVal = String.valueOf(from.getRank());
                }

                else {
                    retVal = this.fileStringMap.get(from.getFile()) + String.valueOf(from.getRank());
                }
            }

        }
        return retVal;
    }


}
