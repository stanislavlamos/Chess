package company.Board;

import company.Game.Move;
import company.Pieces.MasterAbstractPiece;
import company.Pieces.PieaceType;
import company.Pieces.PieceColor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing chess board.
 */
public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //adding serial ID to store board in file

    private static final Integer BOARD_WIDTH = 8;
    private static final Integer BOARD_HEIGHT = 8;
    private Squares[][] boardArray = new Squares[BOARD_WIDTH][BOARD_HEIGHT];
    private final Map<BoardLocation, Squares> locationSquaresMap = new HashMap<>();
    private List<MasterAbstractPiece> enPassantList = new ArrayList<>();
    private ArrayList<Move> moveHistory = new ArrayList<>();
    private ArrayList<String> pgnTagRoster = new ArrayList<>();
    private ArrayList<BoardLocation> movesToAvoidCheck;
    private ArrayList<BoardLocation> kingMovesToAvoidCheck;
    private boolean isCheck;

    /**
     * Constructor for chess board class attributes and initial filling of the chess board.
     */
    public Board(){
        this.isCheck = false;
        Map<BoardLocation, MasterAbstractPiece> pieces = FillBoard.fillBoardWithPieces();
        int colorIndex = 0;
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++){
                SquareColor currentColor = colorIndex % 2 == 0 ? SquareColor.GREEN : SquareColor.YELLOW;
                Squares newSquare = new Squares(currentColor, new BoardLocation(Files.values()[j], i + 1));
                if (pieces.containsKey(newSquare.getLocation())){
                    MasterAbstractPiece piece = pieces.get(newSquare.getLocation());
                    newSquare.setCurrentPieceOnSquare(piece);
                    newSquare.setOccupation(true);
                    piece.setSquares(newSquare);
                }

                boardArray[j][i] = newSquare;
                locationSquaresMap.put(boardArray[j][i].getLocation(), boardArray[j][i]);
                colorIndex++;
            }
            colorIndex++;
        }
    }

    /**
     * Printing formatted chess board on standard output.
     */
    public void printBoardArray() {
        //Iterating over board array to print tags of pieces on corresponding squares on chess board
        for(int i = BOARD_HEIGHT - 1;  i >= 0; i--){
            System.out.print((i + 1) + "  ");
            for (int j = 0; j < BOARD_WIDTH; j++){
                if (boardArray[j][i].isTaken()){
                    MasterAbstractPiece currentPiece = boardArray[j][i].getCurrentPieceOnSquare();
                    System.out.print(currentPiece.getPieceTag() + "  ");
                }else {
                    System.out.print("-  ");
                }
            }
            System.out.println();
        }

        System.out.print("   ");
        for(Files file : Files.values()){
            System.out.print(file.name() + "  ");
        }
        System.out.println();
    }

    /**
     * Getter for Map of BoardLocations and their squares.
     * @return  return map of BoardLocations and their squares
     */
    public Map<BoardLocation, Squares> getLocationSquaresMap(){
        return locationSquaresMap;
    }

    /**
     * Method to print board properties
     */
    public void printBoardText(){
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                System.out.println(boardArray[j][i]);
            }
        }
    }

    /**
     * Method to get the ArrayList of EnPassant locations.
     * @return  enPassantList   list of enPassant locations
     */
    public List<MasterAbstractPiece> getEnPassantList() {
        return enPassantList;
    }

    /**
     * Method to set new entPassant list
     * @param enPassantList new enPassant list
     */
    public void setEnPassantList(ArrayList<MasterAbstractPiece> enPassantList) {
        this.enPassantList = enPassantList;
    }

    /**
     * Method to get the array of squares on the chess board.
     * @return  boardArray  list of squares on chess board
     */
    public Squares[][] getBoardArray() {
        return boardArray;
    }

    /**
     * Method to set new Board array (list of squares on chess board).
     * @param boardArray    new board array to set
     */
    public void setBoardArray(Squares[][] boardArray) {
        this.boardArray = boardArray;
    }

    /**
     * Method to get the list of all past moves.
     * @return  moveHistory history of past moves during the game
     */
    public ArrayList<Move> getMoveHistory() {
        return moveHistory;
    }

    /**
     * Method to add new move to the list of past moves.
     * @param newMove   new move to add to the list of past moves
     */
    public void addMoveHistory(Move newMove) {
        this.moveHistory.add(newMove);
    }


    /**
     * Method to get tag rosters from loaded PGN file.
     * @return  pgnTagRoster    list of all tag rosters from PGN file
     */
    public ArrayList<String> getPgnTagRoster() {
        return pgnTagRoster;
    }

    /**
     * Method to set new list of tag roasters from PGN file.
     * @param pgnTagRoster  new list of tag roasters
     */
    public void setPgnTagRoster(ArrayList<String> pgnTagRoster) {
        this.pgnTagRoster = pgnTagRoster;
    }

    /**
     * Method to set new move history array.
     * @param moveHistory   new move history array
     */
    public void setMoveHistory(ArrayList<Move> moveHistory) {
        this.moveHistory = moveHistory;
    }

    /**
     * Method to handle pawn promotion in terms of PGN annotation.
     * @param pieceTag  tag of piece the pawn was promoted to
     */
    public void handlePawnPromotionPgn(String pieceTag){
        ArrayList<Move> moveHistory = this.getMoveHistory();
        Move lastMove = moveHistory.get(this.getMoveHistory().size() - 1);
        String pgnNotation = lastMove.getPgnNotation();
        String pgnNotationTmp = pgnNotation;
        pgnNotation = pgnNotation + "=" + pieceTag;
        lastMove.setPgnNotation(pgnNotation);
        moveHistory.set(moveHistory.size() - 1, lastMove);
        this.setMoveHistory(moveHistory);
    }

    public ArrayList<BoardLocation> getMovesToAvoidCheck() {
        return movesToAvoidCheck;
    }

    public void setMovesToAvoidCheck(ArrayList<BoardLocation> movesToAvoidCheck) {
        this.movesToAvoidCheck = movesToAvoidCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public Squares getKingLocation(PieceColor kingColor){
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (this.getBoardArray()[j][i].isTaken() && this.getBoardArray()[j][i].getCurrentPieceOnSquare().getPieceType().equals(PieaceType.KING)){
                    MasterAbstractPiece prospectedKing = this.getBoardArray()[j][i].getCurrentPieceOnSquare();

                    if (prospectedKing.getColor().equals(kingColor)){
                        return prospectedKing.getSquares();
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<BoardLocation> getKingMovesToAvoidCheck() {
        return kingMovesToAvoidCheck;
    }

    public void setKingMovesToAvoidCheck(ArrayList<BoardLocation> kingMovesToAvoidCheck) {
        this.kingMovesToAvoidCheck = kingMovesToAvoidCheck;
    }
}
