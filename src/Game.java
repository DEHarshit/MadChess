import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class Game {

    private JFrame frame;
    private final Map<Integer,ImageIcon> iconMap = new HashMap<>();
    private final ImageIcon BBishop = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/BBishop.png")));
    private final ImageIcon WBishop = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/WBishop.png")));
    private final ImageIcon BKnight = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/BKnight.png")));
    private final ImageIcon WKnight = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/WKnight.png")));
    private final ImageIcon BRook = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/BRook.png")));
    private final ImageIcon WRook = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/WRook.png")));
    private final ImageIcon BQueen = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/BQueen.png")));
    private final ImageIcon WQueen = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/WQueen.png")));
    private final ImageIcon BKing = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/BKing.png")));
    private final ImageIcon WKing = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/WKing.png")));
    private final ImageIcon BPawn = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/BPawn.png")));
    private final ImageIcon WPawn = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/pieces/WPawn.png")));
    public Board board;
    private JPanel boardPanel;
    public int[][] boardMatrix;
    final private Point[] selectedTile = {null};
    private String player1;
    private String player2;


    public Game(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;

        iconMap.put(-1, WPawn);
        iconMap.put(-2, WKnight);
        iconMap.put(-3, WBishop);
        iconMap.put(-5, WRook);
        iconMap.put(-9, WQueen);
        iconMap.put(-10, WKing);
        iconMap.put(1, BPawn);
        iconMap.put(2, BKnight);
        iconMap.put(3, BBishop);
        iconMap.put(5, BRook);
        iconMap.put(9, BQueen);
        iconMap.put(10, BKing);

        frame = new JFrame();
        //frame.setSize(1280,720); // later change to pc screen size using toolkit
        frame.setTitle("MadChess");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon image = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/assets/logo.png")));
        frame.setIconImage(image.getImage());
        boardPanel = new JPanel(new GridLayout(8,8));

        frame.add(boardPanel);
        board = new Board(this.player1,this.player2);
        boardMatrix = board.getBoard();

    }

    public void beginGame(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                JLabel tile = new JLabel();
                tile.setBackground((i+j)%2==0 ? Color.white: Color.darkGray);
                tile.setOpaque(true);
                int val = boardMatrix[i][j];
                setTilePiece(tile, val, i,j);
                boardPanel.add(tile);
            }
            frame.pack();
            frame.setVisible(true);
        }
    }

    private void setTilePiece(JLabel tile,int val,int finalI, int finalJ){
        switch (val){
            case 1 -> tile.setIcon(BPawn);
            case -1 -> tile.setIcon(WPawn);
            case 2 -> tile.setIcon(BKnight);
            case -2 -> tile.setIcon(WKnight);
            case 3 -> tile.setIcon(BBishop);
            case -3 -> tile.setIcon(WBishop);
            case 5 -> tile.setIcon(BRook);
            case -5 -> tile.setIcon(WRook);
            case 9 -> tile.setIcon(BQueen);
            case -9 -> tile.setIcon(WQueen);
            case 10 -> tile.setIcon(BKing);
            case -10 -> tile.setIcon(WKing);
        }

        tile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(selectedTile[0] == null && boardMatrix[finalI][finalJ]!=0){
                    selectedTile[0] = new Point(finalI, finalJ);
                    System.out.println("Selected");
                } else if (selectedTile[0]!=null && boardMatrix[selectedTile[0].x][selectedTile[0].y] !=0) {
                    int piece = boardMatrix[selectedTile[0].x][selectedTile[0].y];
                    int tile = boardMatrix[finalI][finalJ];
                    if((tile>0 && piece > 0) || (tile<0 && piece < 0)){ // double click makes piece disappear, set capture rules i.e. white cant capture itself
                        if(boardMatrix[finalI][finalJ]!=0){
                            selectedTile[0] = new Point(finalI, finalJ);
                            System.out.println("Selected New Piece");
                        }
                    }
                    else {
                        if(board.movePiece(selectedTile[0].x,selectedTile[0].y,finalI,finalJ)){
                            System.out.println("Selected Move");
                            //movePiece(finalI,finalJ,piece);
                            refreshBoard();
                            selectedTile[0] = null;
                        }
                        else {
                            System.out.println("False Move");

                            /*if(boardMatrix[finalI][finalJ]!=0){
                                selectedTile[0] = new Point(finalI, finalJ);
                                System.out.println("Selected New Piece");
                            }*/
                        }
                    }
                }
            }
        });
    }

    public void refreshBoard(){
        Component[] components = boardPanel.getComponents();
        int x =0;
        int y =0;
        boardMatrix = board.getBoard();
        for(int i =0;i<64;i++){
            if(y>7) {
                y = 0;
                x += 1;
            }
            JLabel tile = (JLabel) components[i];
            tile.setIcon(null);
            int piece = boardMatrix[x][y];
            if(piece !=0){
                tile.setIcon(iconMap.get(piece));
            }
            y+=1;
        }
    }

    private void movePiece(int dx,int dy,int piece){
        int x = selectedTile[0].x;
        int y = selectedTile[0].y;

        Component[] components = boardPanel.getComponents();
        for(int i =0;i<64;i++){
            if (i == x*8+y){
                JLabel tile = (JLabel) components[i];
                tile.setIcon(null);
            } else if (i == dx*8+dy){
                JLabel tile = (JLabel) components[i];
                tile.setIcon(null);
                if(iconMap.containsKey(piece)){
                    tile.setIcon(iconMap.get(piece));
                }
            }

        }
        boardPanel.repaint();
    }
}
