package company.Game;

import company.Board.Board;
import company.Board.BoardLocation;
import company.Board.Files;
import company.Board.Squares;
import company.Pieces.*;
import company.Player.AbstractPlayer;
import company.Player.PlayerType;
import company.View.BoardTile;
import company.View.FileReader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class holding the attributes for Game Controller.
 */
public class GameModel {
    private Board board;
    private AbstractPlayer abstractPlayer1;
    private AbstractPlayer abstractPlayer2;
    private AbstractPlayer[] playerArr;
    private AbstractPlayer playerOnTurn;
    private GameStates gameState;
    private GameController gameController;
    private boolean pawnPromotion;
    private JFrame windowView;
    private JPanel squarePanel;
    private ChessTimer blackTimer;
    private ChessTimer whiteTimer;
    private Thread blackThread;
    private Thread whiteThread;
    private PgnReader pgnReader;
    private final Logger logger = Logger.getLogger(BoardTile.class.getName());
    private FileReader fileReader;

    /**
     * Constructor of GameController class attributes.
     * @param abstractPlayer1   first player
     * @param abstractPlayer2   second player
     * @board   instance of the board
     */
    public GameModel(AbstractPlayer abstractPlayer1, AbstractPlayer abstractPlayer2, Board board){
        this.abstractPlayer1 = abstractPlayer1;
        this.abstractPlayer2 = abstractPlayer2;
        this.board = board;
        gameController = new GameController(this);
        this.pawnPromotion = false;

        this.fileReader = FileReader.getInstance();
        this.pgnReader = new PgnReader(this);

        this.blackTimer = new ChessTimer(this);
        this.whiteTimer = new ChessTimer(this);
        this.blackThread = new Thread(blackTimer);
        this.whiteThread = new Thread(whiteThread);

        this.blackThread.start();
        this.whiteThread.start();
    }

    /**
     * Geetter for instance of the board.
     * @return Board    intance of the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Setter for the instance of the board.
     * @param board new instance of the board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Getter for Player1.
     * @return instance of the player1
     */
    public AbstractPlayer getAbstractPlayer1() {
        return abstractPlayer1;
    }

    /**
     * Setter for Player1.
     * @param abstractPlayer1   new instance of the Player1
     */
    public void setAbstractPlayer1(AbstractPlayer abstractPlayer1) {
        this.abstractPlayer1 = abstractPlayer1;
    }

    /**
     * Getter for instance of the player2.
     * @return instance of the player2
     */
    public AbstractPlayer getAbstractPlayer2() {
        return abstractPlayer2;
    }

    /**
     * Setter for player2.
     * @param abstractPlayer2 new instance of the player2
     */
    public void setAbstractPlayer2(AbstractPlayer abstractPlayer2) {
        this.abstractPlayer2 = abstractPlayer2;
    }

    /**
     * Getter for array of all players in the game.
     * @return array of all players in the game
     */
    public AbstractPlayer[] getPlayerArr() {
        return playerArr;
    }

    /**
     * Seter for player array.
     * @param playerArr new array of players
     */
    public void setPlayerArr(AbstractPlayer[] playerArr) {
        this.playerArr = playerArr;
    }

    /**
     * Getter for player on turn.
     * @return player on turn in the game
     */
    public AbstractPlayer getPlayerOnTurn() {
        return playerOnTurn;
    }

    /**
     * Set player on turn.
     * @param playerOnTurn instance of the player to set on turn
     */
    public void setPlayerOnTurn(AbstractPlayer playerOnTurn) {
        this.playerOnTurn = playerOnTurn;
    }

    /**
     * Method for changing the players on turn and checking checkmate or castling.
     */
    public void changePlayerOnTurn() {
        this.getBoard().setCheck(false);
        this.getBoard().setKingMovesToAvoidCheck(new ArrayList<BoardLocation>());
        this.getBoard().setMovesToAvoidCheck(new ArrayList<BoardLocation>());

        AbstractPlayer currentPlayerOnTurn = this.getPlayerOnTurn();

        //Stopping timer for player on turn
        if (this.getPlayerOnTurn().getPlayerType().equals(PlayerType.HUMANPLAYER)){
            if (this.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                this.getWhiteTimer().stop();
            }else {
                this.getBlackTimer().stop();
            }
        }

        //Setting player new player on turn
        for (AbstractPlayer player : this.getPlayerArr()){
            if (!(player.equals(currentPlayerOnTurn))){
                this.setPlayerOnTurn(player);
            }
        }

        //Starting the timer for current player on turn
        if (this.getPlayerOnTurn().getPlayerType().equals(PlayerType.HUMANPLAYER)){
            if (this.getPlayerOnTurn().getPlayerPieceColor().equals(PieceColor.WHITE)){
                //this.getWhiteTimer().resume();
                Thread tmpThreadWhite = new Thread(this.getWhiteTimer());
                tmpThreadWhite.start();
                this.getWhiteTimer().resume();
            }else {
                //this.getBlackTimer().resume();
                Thread tmpThreadBlack = new Thread(this.getBlackTimer());
                tmpThreadBlack.start();
                this.getBlackTimer().resume();
            }
        }


        //Checking checkmate
        if (isCheckMate()){
            this.setGameState(GameStates.CHECKMATE);
            this.gameController.endGame();
        }

        //Checking possibility of castling
        if (this.playerOnTurn.getPlayerType().equals(PlayerType.HUMANPLAYER)){
            String castlingState;
            CastlingHandler castlingHandler;

            //Queenside and kingside castling is possible
            if(checkLongCastling() && checkShortCastling()){
                castlingState = "Both";
                castlingHandler = new CastlingHandler(this, castlingState);
            }

            //Queenside castling is possible
            else if(checkLongCastling()){
                castlingState = "Long";
                castlingHandler = new CastlingHandler(this, castlingState);
            }

            //Kingside castling is possible
            else if(checkShortCastling()){
                castlingState = "Short";
                castlingHandler = new CastlingHandler(this, castlingState);
            }
        }

        //Generating valid moves for Computer player
        if(this.playerOnTurn.getPlayerType().equals(PlayerType.COMPUTERPLAYER)){
            MasterAbstractPiece pieceToMove = getStartLocationForComputerPlayerMove();
            List<BoardLocation> validMoves = pieceToMove.getValidMoves(this.board);
            gameController.initiateMove(this.playerOnTurn, pieceToMove.getSquares().getLocation(),validMoves.get(0));
        }
    }

    /**
     * Getter for current state of the game.
     * @return current staet of the game
     */
    public GameStates getGameState() {
        return gameState;
    }

    /**
     * Setter for state of the game.
     * @param gameState new instance of the gameState
     */
    public void setGameState(GameStates gameState) {
        this.gameState = gameState;
    }

    /**
     * Getter for gameController.
     * @return instance of the game controller
     */
    public GameController getGameController() {
        return gameController;
    }

    /**
     * Check whether pawn promotion has occured.
     * @return indicator whether pawn promotion has occurred
     */
    public boolean isPawnPromotion() {
        return pawnPromotion;
    }

    /**
     * Setter for pawn promotion indicator.
     * @param pawnPromotion
     */
    public void setPawnPromotion(boolean pawnPromotion) {
        this.pawnPromotion = pawnPromotion;
    }

    /**
     * Getter for window of the main game.
     * @return  window of the main game
     */
    public JFrame getWindowView() {
        return windowView;
    }

    /**
     * Setter for window of the main game.
     * @param windowView new instance of the main game window
     */
    public void setWindowView(JFrame windowView) {
        this.windowView = windowView;
    }

    /**
     * Getter for square panel holding GUI squares of chess board.
     * @return  square panel holding GUI squares of chess board
     */
    public JPanel getSquarePanel() {
        return squarePanel;
    }

    /**
     * Setter of the square panel holding GUI squares of chess board.
     * @param squarePanel new instance of the square panel holding GUI squares of chess board
     */
    public void setSquarePanel(JPanel squarePanel) {
        this.squarePanel = squarePanel;
    }

    /**
     * Generating piece to move for computer player.
     * @return piece to move for the computer player
     */
    private MasterAbstractPiece getStartLocationForComputerPlayerMove(){
        PieceColor computerPlayerPieceColor = this.playerOnTurn.getPlayerPieceColor();

        //Iterating over the board
        for (Integer i = 0; i < 8; i++){
            for(Integer j = 0; j < 8; j++){
                Squares square = this.board.getLocationSquaresMap().get(new BoardLocation(Files.values()[j], i + 1));

                //Checking whether the prospected piece has legal moves and has the same color as computer player
                if (square.isTaken() && square.getCurrentPieceOnSquare().getColor().equals(computerPlayerPieceColor)){
                    MasterAbstractPiece prospectedPiece = square.getCurrentPieceOnSquare();

                    if (prospectedPiece.getValidMoves(this.board).size() != 0){
                        return prospectedPiece;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Checking kingside castling.
     * @return indicator whether the kingside castling is possible or not
     */
    private boolean checkShortCastling(){
        PieceColor colorOnTurn = this.playerOnTurn.getPlayerPieceColor();

        //Checking kingside castling for white pieces
        if (colorOnTurn.equals(PieceColor.WHITE)){
            MasterAbstractPiece kingPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.E, 1)).getCurrentPieceOnSquare();
            MasterAbstractPiece rookPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.H, 1)).getCurrentPieceOnSquare();

            if (kingPiece != null && kingPiece.getPieceType().equals(PieaceType.KING) && kingPiece.getColor().equals(colorOnTurn)
                    && rookPiece != null && rookPiece.getPieceType().equals(PieaceType.ROOK) && rookPiece.getColor().equals(colorOnTurn)){

                BoardLocation kingStart = kingPiece.getSquares().getLocation();
                BoardLocation endangeredSquare1 = new BoardLocation(Files.G, 1);
                BoardLocation endangeredSquare2 = new BoardLocation(Files.F, 1);

                boolean endangeredSquare1Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare1).isTaken();
                boolean endangeredSquare2Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare2).isTaken();

                if (!endangeredSquare1Occupancy && !endangeredSquare2Occupancy
                        && !isLocationEndangered(endangeredSquare1, PieceColor.BLACK) && !isLocationEndangered(endangeredSquare2, PieceColor.BLACK)
                        && !isLocationEndangered(kingStart, PieceColor.BLACK)){
                    return true;
                }
            }
        }

        //Checking kingside castling for black pieces
        else if (colorOnTurn.equals(PieceColor.BLACK)){
            MasterAbstractPiece kingPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.E, 8)).getCurrentPieceOnSquare();
            MasterAbstractPiece rookPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.H, 8)).getCurrentPieceOnSquare();

            if (kingPiece != null && kingPiece.getPieceType().equals(PieaceType.KING) && kingPiece.getColor().equals(colorOnTurn)
                    && rookPiece != null && rookPiece.getPieceType().equals(PieaceType.ROOK) && rookPiece.getColor().equals(colorOnTurn)){

                BoardLocation kingStart = kingPiece.getSquares().getLocation();
                BoardLocation endangeredSquare1 = new BoardLocation(Files.G, 8);
                BoardLocation endangeredSquare2 = new BoardLocation(Files.F, 8);

                boolean endangeredSquare1Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare1).isTaken();
                boolean endangeredSquare2Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare2).isTaken();

                if (!endangeredSquare1Occupancy && !endangeredSquare2Occupancy
                        && !isLocationEndangered(endangeredSquare1, PieceColor.WHITE) && !isLocationEndangered(endangeredSquare2, PieceColor.WHITE)
                        && !isLocationEndangered(kingStart, PieceColor.WHITE)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checking queenside castling.
     * @return indicator whether the queenside castling is possible or not
     */
    private boolean checkLongCastling(){
        PieceColor colorOnTurn = this.playerOnTurn.getPlayerPieceColor();

        //Checking queenside castling for white pieces
        if (colorOnTurn.equals(PieceColor.WHITE)){
            MasterAbstractPiece kingPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.E, 1)).getCurrentPieceOnSquare();
            MasterAbstractPiece rookPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.A, 1)).getCurrentPieceOnSquare();

            if (kingPiece != null && kingPiece.getPieceType().equals(PieaceType.KING) && kingPiece.getColor().equals(colorOnTurn)
                    && rookPiece != null && rookPiece.getPieceType().equals(PieaceType.ROOK) && rookPiece.getColor().equals(colorOnTurn)){

                BoardLocation kingStart = kingPiece.getSquares().getLocation();
                BoardLocation endangeredSquare1 = new BoardLocation(Files.D, 1);
                BoardLocation endangeredSquare2 = new BoardLocation(Files.C, 1);
                BoardLocation endangeredSquare3 = new BoardLocation(Files.B, 1);

                boolean endangeredSquare1Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare1).isTaken();
                boolean endangeredSquare2Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare2).isTaken();
                boolean endangeredSquare3Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare3).isTaken();

                if (!endangeredSquare1Occupancy && !endangeredSquare2Occupancy && !endangeredSquare3Occupancy
                        && !isLocationEndangered(endangeredSquare1, PieceColor.BLACK) && !isLocationEndangered(endangeredSquare2, PieceColor.BLACK)
                        && !isLocationEndangered(kingStart, PieceColor.BLACK)){
                    return true;
                }
            }
        }

        //Checking queenside castling for black pieces
        else if (colorOnTurn.equals(PieceColor.BLACK)){
            MasterAbstractPiece kingPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.E, 8)).getCurrentPieceOnSquare();
            MasterAbstractPiece rookPiece = this.board.getLocationSquaresMap().get(new BoardLocation(Files.A, 8)).getCurrentPieceOnSquare();

            if (kingPiece != null && kingPiece.getPieceType().equals(PieaceType.KING) && kingPiece.getColor().equals(colorOnTurn)
                    && rookPiece != null && rookPiece.getPieceType().equals(PieaceType.ROOK) && rookPiece.getColor().equals(colorOnTurn)){

                BoardLocation kingStart = kingPiece.getSquares().getLocation();
                BoardLocation endangeredSquare1 = new BoardLocation(Files.D, 8);
                BoardLocation endangeredSquare2 = new BoardLocation(Files.C, 8);
                BoardLocation endangeredSquare3 = new BoardLocation(Files.B, 8);

                boolean endangeredSquare1Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare1).isTaken();
                boolean endangeredSquare2Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare2).isTaken();
                boolean endangeredSquare3Occupancy = this.board.getLocationSquaresMap().get(endangeredSquare3).isTaken();

                if (!endangeredSquare1Occupancy && !endangeredSquare2Occupancy && !endangeredSquare3Occupancy
                        && !isLocationEndangered(endangeredSquare1, PieceColor.WHITE) && !isLocationEndangered(endangeredSquare2, PieceColor.WHITE)
                        && !isLocationEndangered(kingStart, PieceColor.WHITE)){
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Checking whether the location of kingside castling is endangered by opponent's piece.
     * @param location  location of square which can be endangered
     * @param opponentColor opponet's color
     * @return indicator whether the location is endangered or not
     */
    private boolean isLocationEndangered(BoardLocation location, PieceColor opponentColor){
        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                Squares squareToWatch = this.board.getBoardArray()[j][i];

                //Checking whether the location of the square is in opponents legal moves
                if (squareToWatch.isTaken() && squareToWatch.getCurrentPieceOnSquare().getColor().equals(opponentColor)){
                    for (BoardLocation validMove : squareToWatch.getCurrentPieceOnSquare().getValidMoves(this.board)){
                        if (validMove.equals(location)){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    /**
     * Getter to get timer of the player with black pieces.
     * @return  timer of the player with black pieces
     */
    public ChessTimer getBlackTimer() {
        return blackTimer;
    }

    /**
     * Getter to get timer of the player with white pieces.
     * @return  timer of the player with white pieces
     */
    public ChessTimer getWhiteTimer() {
        return whiteTimer;
    }

    /**
     * Getter for thread of the player with black pieces.
     * @return  thread of the player with black pieces
     */
    public Thread getBlackThread() {
        return blackThread;
    }

    /**
     * Getter for thread of the player with white pieces.
     * @return  thread of the player with white pieces
     */
    public Thread getWhiteThread() {
        return whiteThread;
    }

    /**
     * Method to kill all threads.
     */
    public void killAllThreads(){
        this.getBlackTimer().stop();
        this.getWhiteTimer().stop();
    }

    /**
     * Method to get instance of the PGNReader class.
     * @return  instance of the PGNReader class
     */
    public PgnReader getPgnReader() {
        return pgnReader;
    }

    /**
     * Setter for the PGNReader class.
     * @param pgnReader new instance of the PGNReader class
     */
    public void setPgnReader(PgnReader pgnReader) {
        this.pgnReader = pgnReader;
    }

    /**
     * Checking whether checkmate has occured.
     * @return boolean whether checkmte has occured or not
     */
    private boolean isCheckMate() {
        Integer counter = 0;
        PieceColor kingColor = this.getPlayerOnTurn().getPlayerPieceColor();
        Squares kingSquare = this.getBoard().getKingLocation(kingColor);

        //Iterating over the board and checking whether king is endangered
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
                Squares squareToWatch = this.board.getBoardArray()[j][i];

                if (squareToWatch.isTaken() && !(squareToWatch.getCurrentPieceOnSquare().getColor().equals(kingColor))) {
                    List<BoardLocation> opponentsMoves = squareToWatch.getCurrentPieceOnSquare().getValidMoves(this.board);

                    if (opponentsMoves.contains(kingSquare.getLocation())) {
                        counter++;
                        resolveCheck(kingColor, squareToWatch.getCurrentPieceOnSquare());
                    }
                }
            }
        }

        //Checking number of possible moves
        if (counter == 0){
            return false;
        }

        //Checking whether some moves can prevent king from being checkmated
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
                Squares squareToWatch = this.board.getBoardArray()[j][i];

                if (squareToWatch.isTaken() && (squareToWatch.getCurrentPieceOnSquare().getColor().equals(kingColor))) {
                    MasterAbstractPiece pieceaToAvoidCheckMate = squareToWatch.getCurrentPieceOnSquare();

                    for (BoardLocation possibleMoveToAvoidCheck : pieceaToAvoidCheckMate.getValidMoves(this.getBoard())){
                        if (this.getBoard().getMovesToAvoidCheck().contains(possibleMoveToAvoidCheck)
                                && !pieceaToAvoidCheckMate.getPieceType().equals(PieaceType.KING)){
                            this.getBoard().setCheck(true);
                            return false;
                        }
                    }
                }
            }
        }

        //Checking whether king's possible moves do not result in checkmate
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
                Squares squareToWatch = this.board.getBoardArray()[j][i];

                if (squareToWatch.isTaken() && !(squareToWatch.getCurrentPieceOnSquare().getColor().equals(kingColor))) {
                    MasterAbstractPiece pieceaToTakeKing = squareToWatch.getCurrentPieceOnSquare();

                    for (BoardLocation kingMove : this.getBoard().getKingMovesToAvoidCheck()) {
                        MasterAbstractPiece originalPiece = this.getBoard().getLocationSquaresMap().get(kingMove).getCurrentPieceOnSquare();
                        this.getGameController().updateBoard(kingSquare.getLocation(), kingMove);

                        if (pieceaToTakeKing.getValidMoves(this.getBoard()).contains(kingMove)) {
                            this.getBoard().setCheck(false);
                            return true;
                        }

                        this.getGameController().updateBoard(kingMove, kingSquare.getLocation());
                        if (originalPiece != null){
                            this.getBoard().getLocationSquaresMap().get(kingMove).setCurrentPieceOnSquare(originalPiece);
                        }
                    }
                }
            }
        }

        //Checking the size of king's escape moves
        if (this.getBoard().getKingMovesToAvoidCheck().size() != 0){
            this.getBoard().setCheck(true);
            return false;
        }else {
            this.getBoard().setCheck(false);
            return true;
        }
    }

    /**
     * Method to resolve check and find possible moves.
     * @param kingColor color of King piece
     * @param endangeringPiece  piece endangering the King
     */
    private void resolveCheck(PieceColor kingColor, MasterAbstractPiece endangeringPiece){
        Squares kingSquare = this.getBoard().getKingLocation(kingColor);

        ArrayList<BoardLocation> movesToAvoidCheck = this.getBoard().getMovesToAvoidCheck();
        ArrayList<BoardLocation> kingMovesToAvoidCheck = this.getBoard().getKingMovesToAvoidCheck();

        List<BoardLocation> kingValidMoves = kingSquare.getCurrentPieceOnSquare().getValidMoves(this.getBoard());

        //Adding king valid moves which can help escape check
        for (BoardLocation kingValidMove : kingValidMoves){
            if (!endangeringPiece.getValidMoves(this.getBoard()).contains(kingValidMove) && !kingMovesToAvoidCheck.contains(kingValidMove)){
                kingMovesToAvoidCheck.add(kingValidMove);
            }
        }

        //Adding endangering piece location as possible move to escape check
        if (!movesToAvoidCheck.contains(endangeringPiece.getSquares().getLocation())){
            movesToAvoidCheck.add(endangeringPiece.getSquares().getLocation());
        }

        this.getBoard().setMovesToAvoidCheck(movesToAvoidCheck);
        this.getBoard().setKingMovesToAvoidCheck(kingMovesToAvoidCheck);

        BoardLocation kingLocation = kingSquare.getLocation();
        BoardLocation endangeringPieceLocation = endangeringPiece.getSquares().getLocation();

        //Same column
        if (kingLocation.getFile().equals(endangeringPieceLocation.getFile())){
            if (kingLocation.getRank() > endangeringPieceLocation.getRank()){
                getMovesBetweenKingAndEndangerer(kingLocation, -1, 0);
            }else {
                getMovesBetweenKingAndEndangerer(kingLocation, 1, 0);
            }
        }

        //Same row
        else if (kingLocation.getRank().equals(endangeringPieceLocation.getRank())){
            if (kingLocation.getFile().ordinal() > endangeringPieceLocation.getFile().ordinal()){
                getMovesBetweenKingAndEndangerer(kingLocation, 0, -1);
            }else {
                getMovesBetweenKingAndEndangerer(kingLocation, 0, 1);
            }
        }

        //Diagonal position
        else{
            //Top left
            if (kingLocation.getFile().ordinal() > endangeringPieceLocation.getFile().ordinal()
                    && kingLocation.getRank() < endangeringPieceLocation.getRank()){
                getMovesBetweenKingAndEndangerer(kingLocation, -1, 1);
            }

            //Top right
            else if (kingLocation.getFile().ordinal() < endangeringPieceLocation.getFile().ordinal()
                    && kingLocation.getRank() < endangeringPieceLocation.getRank()){
                getMovesBetweenKingAndEndangerer(kingLocation, -1, -1);
            }

            //Bottom right
            else if (kingLocation.getFile().ordinal() < endangeringPieceLocation.getFile().ordinal()
                    && kingLocation.getRank() > endangeringPieceLocation.getRank()){
                getMovesBetweenKingAndEndangerer(kingLocation, 1, -1);
            }

            //Bottom left
            else {
                getMovesBetweenKingAndEndangerer(kingLocation, 1, 1);
            }

        }
    }

    /**
     * Method to find valid moves between king and endangering piece.
     * @param kingLocation  location of the King
     * @param rankOffset    Y coordinate offset
     * @param fileOffset    X coordinate offset
     */
    private void getMovesBetweenKingAndEndangerer(BoardLocation kingLocation, int rankOffset, int fileOffset){
        Map<BoardLocation, Squares> squareMap = this.getBoard().getLocationSquaresMap();
        PieceColor kingColor = this.getBoard().getLocationSquaresMap().get(kingLocation).getCurrentPieceOnSquare().getColor();
        try {
            BoardLocation next = LocationUtils.createBoardLocation(kingLocation, fileOffset, rankOffset);

            //Iterating over possible move candidates and checking its validity
            while (squareMap.containsKey(next)) {
                if (squareMap.get(next).isTaken()) {
                    if (squareMap.get(next).getCurrentPieceOnSquare().getColor().equals(kingColor)){
                        break;
                    }

                    if (!this.getBoard().getMovesToAvoidCheck().contains(next)){
                        this.getBoard().getMovesToAvoidCheck().add(next);
                        break;
                    }
                }

                else if (!this.getBoard().getMovesToAvoidCheck().contains(next)){
                    this.getBoard().getMovesToAvoidCheck().add(next);
                }
                next = LocationUtils.createBoardLocation(next, fileOffset, rankOffset);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }

    public FileReader getFileReader() {
        return fileReader;
    }

    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }
}
