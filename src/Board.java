import java.util.Arrays;

public class Board {
    public String player1;
    public String player2;

    /*
        Pawn = 1
        Knight = 2
        Bishop = 3
        Rook = 5
        Queen = 9
        King = 10

        -ve for white
     */

    private int[][] board = {
            { 5, 2, 3, 9, 10, 3, 2, 5},
            { 1, 1, 1, 1,  1, 1, 1, 1},
            { 0, 0, 0, 0,  0, 0, 0, 0},
            { 0, 0, 0, 0,  0, 0, 0, 0},
            { 0, 0, 0, 0,  0, 0, 0, 0},
            { 0, 0, 0, 0,  0, 0, 0, 0},
            {-1,-1,-1,-1, -1,-1,-1,-1},
            {-5,-2,-3,-9,-10,-3,-2,-5}
    };

    Board( String player1, String player2){
        this.player1 = player1;
        this.player2 = player2;
    }

    public void beginGame(){
        for(int[] row : this.board){
            for(int piece : row){
                System.out.print(piece + " ");
            }
            System.out.println("");
        }
    }

    public int[][] getBoard(){
        return this.board;
    }

    public boolean movePiece(int x, int y, int dx, int dy){
        int piece = board[x][y];
        this.board[x][y] = 0;
        this.board[dx][dy] = piece;
        beginGame();
        return true;
    }

}
