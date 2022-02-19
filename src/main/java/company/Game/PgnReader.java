package company.Game;

import company.Board.BoardLocation;
import company.Board.Files;
import company.Board.Squares;
import company.Pieces.*;
import company.Player.AbstractPlayer;
import company.Player.PlayerType;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to read games stored in PGN format.
 */
public class PgnReader {
    private GameModel gameModel;
    private String path;
    private ArrayList<String> listOfMoves;
    private Map<String, PieaceType> tagPieceTypeMap;
    private Map<String, Files> stringFileMap;
    private PieaceType pieceOntheMove;
    private BoardLocation fromLocation;
    private BoardLocation toLocation;
    private PieaceType pieceToPromote;
    private PieceColor movingPieceColor;
    private String move;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor of the PgnReader class.
     * @param gameModel instance of the gameModel
     */
    public PgnReader(GameModel gameModel){
        this.gameModel = gameModel;

        fillTagPieceTypeMap();
        fillStringFileMap();
    }

    /**
     * Method to read PGN data from given path.
     * @param path  path to File with PGN data
     * @return  boolean indicator whether the reading of PGN file was successful
     */
    public boolean readPgnFile(String path){
        boolean retVal = true;
        this.path = path;
        try {
            File pgnFile = new File(this.path);
            Scanner pgnReader = new Scanner(pgnFile);
            Boolean tagRosterStatus = true;
            this.listOfMoves = new ArrayList<>();

            //Reading every line in given file and parsing individual moves
            String line;
            while (pgnReader.hasNextLine()) {
                if (!(line = pgnReader.nextLine()).isEmpty() && tagRosterStatus) {
                    parseTagRoster(line);
                    continue;
                } else if (line.isEmpty() && tagRosterStatus) {
                    tagRosterStatus = false;
                    continue;
                }
                parseIndividualMoves(line);
            }
            pgnReader.close();


            //Iterating over the moves and adjusting the board accordingly
            Integer index = 0;
            for (String move : listOfMoves) {
                this.movingPieceColor = index % 2 == 0 ? PieceColor.WHITE : PieceColor.BLACK;
                this.move = move;

                if (handleCastling(move, index)) {
                    index++;
                    continue;
                }

                determinePieceOnTheMove(move);
                determineToLocation(move);
                determineFromLocation(move);
                makeBoardChanges();
                index++;
            }
        }catch (Exception e){
            logger.log(Level.WARNING, e.toString());
            retVal = false;
        }
        return retVal;
    }

    /**
     * Parsing tag rosters from file.
     * @param line  line to parse from
     */
    private void parseTagRoster(String line){
        String[] words = line.split(" \"");

        //Stripping brackets and spaces
        for (Integer i = 0; i < words.length; i++){
            String strippedWordQuotes = words[i].replaceAll("\"", "");
            String strippedWordFinal = strippedWordQuotes.replaceAll("\\[", "").replaceAll("\\]", "");
            words[i] = strippedWordFinal;
        }

        //Setting name of the player based on tag rosters from PGN file
        if (words[0].equals("White")){
            try{
                gameModel.getAbstractPlayer1().setPlayerName(words[1]);
            }catch (Exception e){
                String name = gameModel.getAbstractPlayer1().getPlayerType().equals(PlayerType.COMPUTERPLAYER) ? "Computer player" : "Player1";
                gameModel.getAbstractPlayer1().setPlayerName(name);
            }
        }

        else if(words[0].equals("Black")){
            try{
                gameModel.getAbstractPlayer2().setPlayerName(words[1]);
            }catch (Exception e){
                String name = gameModel.getAbstractPlayer2().getPlayerType().equals(PlayerType.COMPUTERPLAYER) ? "Computer player" : "Player2";
                gameModel.getAbstractPlayer2().setPlayerName(name);
            }
        }

        //Reading the game result from PGN file
        if(words[0].equals("Result")){
            String result = words[1];

            if (result.equals("1-0")){
                gameModel.setGameState(GameStates.WHITE_WIN);
            }

            else if(result.equals("0-1")){
                gameModel.setGameState(GameStates.BLACK_WIN);
            }

            else if(result.equals("1/2-1/2")){
                gameModel.setGameState(GameStates.DRAW);
            }
        }

        ArrayList<String> pgnTagRoster = gameModel.getBoard().getPgnTagRoster();
        pgnTagRoster.add(line);
        gameModel.getBoard().setPgnTagRoster(pgnTagRoster);
    }

    /**
     * Method to parse individual moves.
     * @param line line to parse moves from
     */
    private void parseIndividualMoves(String line){
        String[] moves = line.split(" ");
        String newMove = null;

        //Iterating over the moves
        for (String move : moves){
            try{
                int dotIndex = move.indexOf(".");
                String possibleNumber = move.substring(0, dotIndex);
                Integer.parseInt(possibleNumber);
                newMove = move.substring(dotIndex + 1).strip().replaceAll("#", "").replaceAll("\\+", "").replaceAll("!", "").replaceAll("\\?", "");
                String newMoveTmp = newMove;

                if (!newMoveTmp.trim().isEmpty() && !newMove.equals("1/2-1/2") && !newMove.equals("1-0") && !newMove.equals("0-1")){
                    listOfMoves.add(newMove);
                }

            }catch (Exception e){
                newMove = move.strip().replaceAll("#", "").replaceAll("\\+", "").replaceAll("!", "").replaceAll("\\?", "");
                String newMoveTmp = newMove;

                if (!newMoveTmp.trim().isEmpty() && !newMove.equals("1/2-1/2") && !newMove.equals("1-0") && !newMove.equals("0-1")){
                    listOfMoves.add(newMove);
                }
            }
        }
    }

    /**
     * Method to handle castling when it was detected in PGN file.
     * @param move  current move
     * @param index integer to index the move
     * @return return true if castling was successful
     */
    private boolean handleCastling(String move, Integer index){
        Boolean retVal = false;

        //Handling kingside castling
        if (move.equals("O-O")){
            Integer rank = this.movingPieceColor.equals(PieceColor.WHITE) ? 1 : 8;

            BoardLocation fromKing = new BoardLocation(Files.E, rank);
            BoardLocation toKing = new BoardLocation(Files.G, rank);
            BoardLocation fromRook = new BoardLocation(Files.H, rank);
            BoardLocation toRook = new BoardLocation(Files.F, rank);

            gameModel.getGameController().updateBoard(fromKing, toKing);
            gameModel.getGameController().updateBoard(fromRook, toRook);

            MasterAbstractPiece castlingKing = new King(this.movingPieceColor);
            AbstractPlayer playerOnTurn = gameModel.getAbstractPlayer1().getPlayerPieceColor().equals(this.movingPieceColor) ? gameModel.getAbstractPlayer1() : gameModel.getAbstractPlayer2();
            Move newMoveInstance = new Move(playerOnTurn, fromKing, toKing, castlingKing, move);
            gameModel.getBoard().addMoveHistory(newMoveInstance);

            retVal = true;
        }

        //Handling queenside castling
        else if(move.equals("O-O-O")){
            Integer rank = this.movingPieceColor.equals(PieceColor.WHITE) ? 1 : 8;

            BoardLocation fromKing = new BoardLocation(Files.E, rank);
            BoardLocation toKing = new BoardLocation(Files.C, rank);
            BoardLocation fromRook = new BoardLocation(Files.A, rank);
            BoardLocation toRook = new BoardLocation(Files.D, rank);

            gameModel.getGameController().updateBoard(fromKing, toKing);
            gameModel.getGameController().updateBoard(fromRook, toRook);

            MasterAbstractPiece castlingKing = new King(this.movingPieceColor);
            AbstractPlayer playerOnTurn = gameModel.getAbstractPlayer1().getPlayerPieceColor().equals(this.movingPieceColor) ? gameModel.getAbstractPlayer1() : gameModel.getAbstractPlayer2();
            Move newMoveInstance = new Move(playerOnTurn, fromKing, toKing, castlingKing, move);
            gameModel.getBoard().addMoveHistory(newMoveInstance);

            retVal = true;
        }
        return retVal;
    }

    /**
     * Determine the type of the piece on the move.
     * @param move  current move
     */
    private void determinePieceOnTheMove(String move){
        String pieceTag = move.substring(0, 1);

        //Determining piece type based on piece tag in PGN notation
        if (Character.isUpperCase(pieceTag.charAt(0))){
            this.pieceOntheMove = this.tagPieceTypeMap.get(pieceTag);
        } else{
            this.pieceOntheMove = PieaceType.PAWN;
        }
    }

    /**
     * Method to determine destination location from PGN move.
     * @param move current move
     */
    private void determineToLocation(String move){
        Integer moveStringSize = move.length();

        //Handle pawn promotion
        if (!move.contains("=")){
            Integer destinationRank = Integer.parseInt(move.substring(moveStringSize - 1));
            String destinationFileString = move.substring(moveStringSize - 2, moveStringSize - 1);
            Files destinationFile = stringFileMap.get(destinationFileString);

            this.toLocation = new BoardLocation(destinationFile, destinationRank);

            this.pieceToPromote = null;
        }else {
            //Parse destination location from PGN move
            Integer destinationRank = Integer.parseInt(move.substring(moveStringSize - 3, moveStringSize - 2));
            String destinationFileString = move.substring(moveStringSize - 4, moveStringSize - 3);
            Files destinationFile = stringFileMap.get(destinationFileString);

            this.toLocation = new BoardLocation(destinationFile, destinationRank);

            this.pieceToPromote = tagPieceTypeMap.get(move.substring(moveStringSize - 1));
        }
    }

    /**
     * Method to determine source location based on PGN notation.
     * @param move  current move
     */
    private void determineFromLocation(String move){
        Integer moveStringSize = move.length();
        String fromMove;
        String fromMoveTmp;

        //Parse source location based on pawn promotion state
        if (pieceToPromote != null){
            fromMoveTmp = move.substring(0, moveStringSize - 4);
        }else {
            fromMoveTmp = move.substring(0, moveStringSize - 2);
        }

        fromMove = pieceOntheMove.equals(PieaceType.PAWN) ? fromMoveTmp.substring(0).replaceAll("x", "") : fromMoveTmp.substring(1).replaceAll("x", "");

        //Determine source location and handling ambiguities

        if (fromMove.length() == 0){
            this.fromLocation = getFromLocationPlain();
        }

        else if (fromMove.length() == 1){
            try{
                Integer sourceRank = Integer.parseInt(fromMove);
                Files sourceFile = getFile(sourceRank);

                this.fromLocation = new BoardLocation(sourceFile, sourceRank);
            }catch (Exception e){
                Files sourceFile = stringFileMap.get(fromMove);
                Integer sourceRank = getRank(sourceFile);

                this.fromLocation = new BoardLocation(sourceFile, sourceRank);
            }
        }

        else if (fromMove.length() == 2){
            String sourceFileString = fromMove.substring(0, 1);
            String sourceRankString = fromMove.substring(1);
            Files sourceFile = stringFileMap.get(sourceFileString);
            Integer sourceRank = Integer.parseInt(sourceRankString);

            this.fromLocation = new BoardLocation(sourceFile, sourceRank);
        }
    }

    /**
     * Filling piece tag to piece type map.
     */
    private void fillTagPieceTypeMap(){
        this.tagPieceTypeMap = new HashMap<>();

        this.tagPieceTypeMap.put("P", PieaceType.PAWN);
        this.tagPieceTypeMap.put("K", PieaceType.KING);
        this.tagPieceTypeMap.put("N", PieaceType.KNIGHT);
        this.tagPieceTypeMap.put("B", PieaceType.BISHOP);
        this.tagPieceTypeMap.put("R", PieaceType.ROOK);
        this.tagPieceTypeMap.put("Q", PieaceType.QUEEN);
    }

    /**
     * Filling String to File map.
     */
    private void fillStringFileMap(){
        this.stringFileMap = new HashMap<>();

        this.stringFileMap.put("a", Files.A);
        this.stringFileMap.put("b", Files.B);
        this.stringFileMap.put("c", Files.C);
        this.stringFileMap.put("d", Files.D);
        this.stringFileMap.put("e", Files.E);
        this.stringFileMap.put("f", Files.F);
        this.stringFileMap.put("g", Files.G);
        this.stringFileMap.put("h", Files.H);
    }

    /**
     * Method to get source location in case of no ambiguities.
     * @return source location
     */
    private BoardLocation getFromLocationPlain(){
        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (!gameModel.getBoard().getBoardArray()[j][i].isTaken()){
                    continue;
                }

                Squares currentSquare = gameModel.getBoard().getBoardArray()[j][i];
                MasterAbstractPiece pieceOnSquare = currentSquare.getCurrentPieceOnSquare();
                List<BoardLocation> moveCandidates = pieceOnSquare.getValidMoves(gameModel.getBoard());

                //Getting corresponding source location
                if (pieceOnSquare.getPieceType().equals(pieceOntheMove) && moveCandidates.contains(this.toLocation)
                    && pieceOnSquare.getColor().equals(this.movingPieceColor)){
                    return currentSquare.getLocation();

                }
            }
        }
        return null;
    }

    /**
     * Method to get file coordinate in case of file ambiguity.
     * @param sourceRank rank part of the source location
     * @return corresponding File to given Rank location
     */
    private Files getFile(Integer sourceRank){
        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (!gameModel.getBoard().getBoardArray()[j][i].isTaken()) {
                    continue;
                }

                Squares currentSquare = gameModel.getBoard().getBoardArray()[j][i];
                MasterAbstractPiece pieceOnSquare = currentSquare.getCurrentPieceOnSquare();
                List<BoardLocation> moveCandidates = pieceOnSquare.getValidMoves(gameModel.getBoard());

                //Getting file which corresponds with given rank location
                if (pieceOnSquare.getPieceType().equals(pieceOntheMove) && moveCandidates.contains(this.toLocation)
                        && currentSquare.getLocation().getRank().equals(sourceRank) && pieceOnSquare.getColor().equals(this.movingPieceColor)) {
                    return currentSquare.getLocation().getFile();
                }
            }
        }
        return null;
    }

    /**
     * Method to get rank coordinate in case of rank ambiguity.
     * @param sourceFile    File part of the source location
     * @return  corresponding rank to given File location
     */
    private Integer getRank(Files sourceFile){
        //Iterating over the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (!gameModel.getBoard().getBoardArray()[j][i].isTaken()) {
                    continue;
                }

                Squares currentSquare = gameModel.getBoard().getBoardArray()[j][i];
                MasterAbstractPiece pieceOnSquare = currentSquare.getCurrentPieceOnSquare();
                List<BoardLocation> moveCandidates = pieceOnSquare.getValidMoves(gameModel.getBoard());

                //Getting rank which corresponds with given File location
                if (pieceOnSquare.getPieceType().equals(pieceOntheMove) && moveCandidates.contains(this.toLocation)
                        && currentSquare.getLocation().getFile().equals(sourceFile) && pieceOnSquare.getColor().equals(this.movingPieceColor)) {
                    return currentSquare.getLocation().getRank();
                }
            }
        }
        return null;
    }

    /**
     * Method to make board changes based on PGN move.
     */
    private void makeBoardChanges(){

        //Updating Pawn's first move
        if (gameModel.getBoard().getLocationSquaresMap().get(fromLocation).getCurrentPieceOnSquare().getName().equals("Pawn")) {
            Pawn pawn = (Pawn) gameModel.getBoard().getLocationSquaresMap().get(fromLocation).getCurrentPieceOnSquare();

            if (pawn.isFirstMove()) {
                pawn.setFirstMove(false);
            }
        }


        //Updating board
        gameModel.getGameController().updateBoard(this.fromLocation, this.toLocation);



        //Handling pawn promotion
        if (this.pieceToPromote != null){
            MasterAbstractPiece promotedPiece;

            if (this.pieceToPromote.equals(PieaceType.QUEEN)){
                promotedPiece = new Queen(this.movingPieceColor);
            }

            else if(this.pieceToPromote.equals(PieaceType.BISHOP)){
                promotedPiece = new Bishop(this.movingPieceColor);
            }

            else if (this.pieceToPromote.equals(PieaceType.KNIGHT)){
                promotedPiece = new Knight(this.movingPieceColor);
            }

            else {
                promotedPiece = new Rook(this.movingPieceColor);
            }

            Squares square = gameModel.getBoard().getLocationSquaresMap().get(this.toLocation);
            square.setCurrentPieceOnSquare(promotedPiece);
            gameModel.getGameController().updateBoard(this.toLocation, this.toLocation);
        }
        //Adding move to move history array
        AbstractPlayer playerOnTurn = gameModel.getAbstractPlayer1().getPlayerPieceColor().equals(this.movingPieceColor) ? gameModel.getAbstractPlayer1() : gameModel.getAbstractPlayer2();
        MasterAbstractPiece pieceOnSquare = gameModel.getBoard().getLocationSquaresMap().get(this.toLocation).getCurrentPieceOnSquare();
        Move currentMove = new Move(playerOnTurn, this.fromLocation, this.fromLocation, pieceOnSquare, this.move);
        gameModel.getBoard().addMoveHistory(currentMove);
    }
}
