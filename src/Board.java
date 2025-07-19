import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.abs;
import javax.sound.sampled.*;
import java.util.Objects;

public class Board {
    public String player1;
    public String player2;
    private boolean bKingMoved;
    private boolean wKingMoved;
    private boolean castle;
    private boolean capture;

    public void playSound(String sound){
        switch(sound){
            case "move":
                try {
                    System.out.println("Playing sound");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                            Objects.requireNonNull(getClass().getResource("/assets/sounds/move-self.wav"))
                    );
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            try {
                                audioIn.close();
                            } catch (Exception ignored) {}
                        }
                    });
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                break;
            case "castle":
                try {
                    System.out.println("Playing sound");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                            Objects.requireNonNull(getClass().getResource("/assets/sounds/castle.wav"))
                    );
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            try {
                                audioIn.close();
                            } catch (Exception ignored) {}
                        }
                    });
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                break;
            case "promote":
                try {
                    System.out.println("Playing sound");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                            Objects.requireNonNull(getClass().getResource("/assets/sounds/promote.wav"))
                    );
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            try {
                                audioIn.close();
                            } catch (Exception ignored) {}
                        }
                    });
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                break;
            case "capture":
                try {
                    System.out.println("Playing sound");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                            Objects.requireNonNull(getClass().getResource("/assets/sounds/capture.wav"))
                    );
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            try {
                                audioIn.close();
                            } catch (Exception ignored) {}
                        }
                    });
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                break;
            case "win":
                try {
                    System.out.println("Playing sound");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                            Objects.requireNonNull(getClass().getResource("/assets/sounds/notify.wav"))
                    );
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            try {
                                audioIn.close();
                            } catch (Exception ignored) {}
                        }
                    });
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    public enum Piece {
        PAWN(1), KNIGHT(2), BISHOP(3), ROOK(5), QUEEN(9), KING(10);
        final int value;
        Piece(int v) { value = v; }
    }
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
        this.bKingMoved = false;
        this.wKingMoved = false;
    }

    public void beginGame(){
        for(int[] row : this.board){
            for(int piece : row){
                System.out.print(piece + " ");
            }
            System.out.println("");
        }
    }

    private boolean knightMove(int x, int y, int dx, int dy){
        int[][] knightMoves = {{2,1},{2,-1},{1,2},{-1,2},{1,-2},{-1,-2},{-2,1},{-2,-1}};
        for( int[] row : knightMoves){
            int px = row[0];
            int py = row[1];
            if(x-dx == px && y-dy == py){
                return true;
            }
        }
        return false;
    }

    private boolean bishopMove(int x,int y, int dx, int dy){
        // this one doesn't check if there are pieces between the bishop's move
        /* if(abs(x - dx) == abs(y - dy)){
            return true;
        } */
        if ( x - dx == dy - y && (dx < x && dy > y)){ // top right
            for ( int i =1,j=1; x-i>=0 && y+j<=8;i++,j++){
                 int cx = x-i;
                 int cy = y+j;
                int piece = board[cx][cy];
                 if (cx == dx && cy == dy){
                     break;
                 }
                 else {
                     if(piece != 0){
                         return false;
                     }
                 }
            }
            return true;
        } else if (x - dx == y - dy && (dx < x && dy < y)) { // top left
            for ( int i =1,j=1; x-i>=0 && y-j>=0;i++,j++){
                int cx = x-i;
                int cy = y-j;
                int piece = board[cx][cy];
                if (cx == dx && cy == dy){
                    break;
                }
                else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;

        } else if ( dx - x == dy - y && (dx>x && dy > y)){ // bottom right
            for ( int i =1,j=1; x+i<=8 && y+j<=0;i++,j++){
                int cx = x+i;
                int cy = y+j;
                int piece = board[cx][cy];
                if (cx == dx && cy == dy){
                    break;
                }
                else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;

        } else if ( dx - x == y - dy && (dx > x && dy < y)){ // bottom left
            for ( int i =1,j=1; x+i<=8 && y-j>=0;i++,j++){
                int cx = x+i;
                int cy = y-j;
                int piece = board[cx][cy];
                if (cx == dx && cy == dy){
                    break;
                }
                else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;

        }
        return false;
    }

    private boolean rookMove(int x,int y,int dx,int dy){
        if ( y == dy && ( dx < x )){ // top
            for (int i=1;x-i>=0;i++){
                int cx = x-i;
                int piece = board[cx][y];
                if( cx == dx){
                    break;
                } else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;

        } else if ( y == dy && ( dx > x )){ // bottom
            for (int i=1;x+i>=0;i++){
                int cx = x+i;
                int piece = board[cx][y];
                if( cx == dx){
                    break;
                } else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;

        } else if ( x == dx && ( dy > y )) { // right
            for (int i=1;y+i>=0;i++){
                int cy = y+i;
                int piece = board[x][cy];
                if( cy == dy){
                    break;
                } else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;

        } else if ( x == dx && ( dy < y )){ // left
            for (int i=1;y-i>=0;i++){
                int cy = y-i;
                int piece = board[x][cy];
                if( cy == dy){
                    break;
                } else {
                    if(piece != 0){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean kingMove(int x,int y,int dx, int dy){
        int piece = board[x][y];
        boolean kingMove = piece > 0 ? bKingMoved : wKingMoved;
        if( (x == 0 || x ==7) &&  y ==4 && !kingMove) {
            if (dy == 2) {
                if (board[x][3] == 0 && board[x][2] == 0 && board[x][1] == 0 && abs(board[x][0]) == 5) {
                    board[x][0] = 0;
                    board[x][3] = piece > 0 ? 5 : -5;
                    if (piece > 0){
                        bKingMoved = true;
                    } else {
                        wKingMoved = true;
                    }
                    castle = true;
                    return true;
                }
            } else if (dy == 6) {
                if(board[x][5] == 0 && board[x][6] == 0 && abs(board[x][7]) == 5){
                    board[x][7] = 0;
                    board[x][5] = piece > 0 ? 5 : -5;
                    if (piece > 0){
                        bKingMoved = true;
                    } else {
                        wKingMoved = true;
                    }
                    castle = true;
                    return true;
                }
            }
        }
        if( (abs(dx - x) == 1 && y == dy) || (abs(dy-y) == 1 && x == dx) || (abs(dx-x) == 1 && abs(dy-y) == 1) ){
            if (piece > 0){
                bKingMoved = true;
            } else {
                wKingMoved = true;
            }
            return true;
        }
        return false;
    }

    public int[][] getBoard(){
        return this.board;
    }

    public int movePiece(int x, int y, int dx, int dy, int currentPiece, boolean isBlack){

        int piece = board[x][y];
        if(piece > 0 && !isBlack){
            return 0;
        }
        if(piece <0 && isBlack){
            return 0;
        }
        if(currentPiece != piece){
            return 0;
        }
        boolean swap = false;
        switch(abs(piece)){
            case 1: // pawn
                if(piece < 0){
                    int temp = board[dx][dy]; // piece at destination
                    if(y==dy && temp == 0){ // moving straight for white piece
                        if(x == 6 && (x-dx == 2 || x-dx ==1)){
                            swap = true;
                        } else if (x-dx ==1 ){
                            swap = true;
                        }
                    } else if (abs(y-dy) == 1 && x-dx == 1){ // capture
                        if (temp > 0){ // there is a black piece, +ve piece
                            swap = true;
                        }
                    }
                } else{
                    int temp = board[dx][dy];
                    if(y==dy && temp == 0){
                        if(x == 1 && (dx-x == 2 || dx-x ==1)){
                            swap = true;
                        } else if (dx-x ==1){
                            swap = true;
                        }
                    } else if (abs(y-dy) == 1 && dx-x == 1){
                        if (temp < 0){
                            swap = true;
                        }
                    }
                }
                break;
            case 2: // knight
                if(knightMove(x,y,dx,dy)){
                    swap = true;
                }
                break;
            case 3: // bishop
                if(bishopMove(x,y,dx,dy)){
                    swap = true;
                }
                break;
            case 5: // rook
                if(rookMove(x,y,dx,dy)){
                    swap = true;
                }
                break;
            case 9: // queen
                if(bishopMove(x,y,dx,dy)||rookMove(x,y,dx,dy)){
                    swap = true;
                }
                break;
            case 10: // king
                if(kingMove(x,y,dx,dy)){
                    swap = true;
                }
                break;
        }
        if(swap){
            if((piece == -1 && dx == 0) || (piece == 1 && dx == 7)) {
                piece = piece>0 ? 9 : -9;
                playSound("promote");
            } else if (board[dx][dy]!=0 && abs(board[dx][dy]) != 10) {
                playSound("capture");
            }   else if(castle){
                castle = false;
                playSound("castle");
            }  else if(abs(board[dx][dy]) == 10){
                playSound("win");
            }else {
                playSound("move");
            }
            if(board[dx][dy]==10){
                board[x][y] = 0;
                board[dx][dy] = piece;
                return 2;
            } else if (board[dx][dy] == -10){
                board[x][y] = 0;
                board[dx][dy] = piece;
                return 3;
            }
            board[x][y] = 0;
            board[dx][dy] = piece;
            beginGame();
            return 1;
        }
        return 0;
    }

}
