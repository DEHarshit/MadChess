import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class Game {

    private JFrame frame;
    private int tileSize=60;
    private final int cooldown = 15000;
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
    private Map<JPanel, Long> lastClicked = new HashMap<>();
    private Map<JPanel, Timer> cooldownTimers = new HashMap<>();


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
        frame.setResizable(false);
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
                JPanel tile = new JPanel();
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

    private void setTilePiece(JPanel jTile,int val,int finalI, int finalJ){
        JLayeredPane tile = getLabelTile(val);

        jTile.add(tile);

        jTile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if(selectedTile[0] == null && boardMatrix[finalI][finalJ]!=0){
                    if(currentTime > lastClicked.getOrDefault(jTile,0L) + cooldown){
                        selectedTile[0] = new Point(finalI, finalJ);

                        Component[] components = boardPanel.getComponents();
                        int index = (finalI * 8) + finalJ;

                        JPanel jTile = (JPanel) components[index];
                        Component[] jComponents = jTile.getComponents();

                        JLayeredPane layeredPane = (JLayeredPane) jComponents[0];
                        JLabel highlight = (JLabel) layeredPane.getClientProperty("highlight");
                        highlight.setBackground(new Color(34, 99, 128));
                        highlight.setIcon(null);

                        System.out.println("Selected");
                    } else {
                        System.out.println("On cooldown");
                    }

                } else if (selectedTile[0]!=null && boardMatrix[selectedTile[0].x][selectedTile[0].y] !=0) {
                    int piece = boardMatrix[selectedTile[0].x][selectedTile[0].y];
                    int tile = boardMatrix[finalI][finalJ];
                    if((tile>0 && piece > 0) || (tile<0 && piece < 0)){ // double click makes piece disappear, set capture rules i.e. white cant capture itself

                        if(currentTime > lastClicked.getOrDefault(jTile,0L) + cooldown) {
                            if(boardMatrix[finalI][finalJ]!=0){

                                int x = selectedTile[0].x;
                                int y = selectedTile[0].y;

                                Component[] components = boardPanel.getComponents();
                                int index = (x*8)+y;

                                JPanel jTile = (JPanel) components[index];
                                Component[] jComponents = jTile.getComponents();

                                JLayeredPane layeredPane = (JLayeredPane) jComponents[0];
                                JLabel highlight = (JLabel) layeredPane.getClientProperty("highlight");
                                highlight.setBackground(null);
                                highlight.setIcon(null);

                                JPanel jNewTile = (JPanel) components[(finalI * 8) + finalJ];
                                Component[] jNewComponents = jNewTile.getComponents();

                                JLayeredPane newPane = (JLayeredPane) jNewComponents[0];
                                JLabel newTile = (JLabel) newPane.getClientProperty("highlight");
                                newTile.setBackground(new Color(34, 99, 128));
                                newTile.setIcon(null);

                                selectedTile[0] = new Point(finalI, finalJ);
                                System.out.println("Selected New Pieces");
                            }
                        } else {
                            System.out.println("On cooldown");
                        }

                    }
                    else {
                        if(board.movePiece(selectedTile[0].x,selectedTile[0].y,finalI,finalJ)){

                            Component[] components = boardPanel.getComponents();
                            int index = (finalI * 8) + finalJ;
                            lastClicked.put((JPanel) components[index],System.currentTimeMillis());

                            JPanel jTile = (JPanel) components[index];
                            Component[] jComponents = jTile.getComponents();
                            JLayeredPane layeredPane = (JLayeredPane) jComponents[0];

                            JLabel cooldownOverlay = (JLabel) layeredPane.getClientProperty("cooldown");
                            cooldownOverlay.setVisible(true);

                            Timer oldTimer = cooldownTimers.get(jTile);
                            if (oldTimer != null) {
                                oldTimer.stop();
                            }

                            Timer timer = new Timer(1000,null);
                            cooldownTimers.put(jTile,timer);
                            final long[] timeLeft = {cooldown/1000};
                            final int constant = (int)(tileSize/timeLeft[0]);

                            timer.addActionListener((event) -> {
                                if (timeLeft[0] <= 0) {
                                    cooldownOverlay.setVisible(false);
                                    ((Timer) event.getSource()).stop();
                                } else {
                                    cooldownOverlay.setBounds((int) (0),(int)(tileSize - constant*timeLeft[0]),tileSize,tileSize);
                                    timeLeft[0]--;
                                }
                            });
                            timer.setInitialDelay(0);
                            timer.start();

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

    private JLayeredPane getLabelTile(int val) {
        JLayeredPane JLTile = new JLayeredPane();
        JLTile.setPreferredSize(new Dimension(tileSize,tileSize));

        JLabel tile = new JLabel();
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
        tile.setHorizontalAlignment(JLabel.CENTER);
        tile.setVerticalAlignment(JLabel.CENTER);
        tile.setBounds(0, 0, tileSize, tileSize);

        JLabel tileHightlight = new JLabel();
        tileHightlight.setBounds(0, 0, tileSize, tileSize);
        tileHightlight.setOpaque(true);
        tileHightlight.setBackground(new Color(0,0,0,0));

        JLabel cooldownOverlay = new JLabel();
        cooldownOverlay.setBounds(0,0,tileSize,tileSize);
        cooldownOverlay.setOpaque(true);
        cooldownOverlay.setBackground(new Color(0,0,0,120));
        cooldownOverlay.setVisible(false);

        JLTile.add(tileHightlight,JLayeredPane.DEFAULT_LAYER);
        JLTile.add(cooldownOverlay,JLayeredPane.PALETTE_LAYER);
        JLTile.add(tile,JLayeredPane.MODAL_LAYER);

        JLTile.putClientProperty("highlight", tileHightlight);
        JLTile.putClientProperty("icon", tile);
        JLTile.putClientProperty("cooldown",cooldownOverlay);

        return JLTile;
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
            JPanel jTile = (JPanel) components[i];
            Component[] jComponents = jTile.getComponents();

            JLayeredPane JLTile = (JLayeredPane) jComponents[0];

            JLabel tile = (JLabel) JLTile.getClientProperty("icon");
            tile.setIcon(null);
            tile.setBorder(null);

            JLabel highlight = (JLabel) JLTile.getClientProperty("highlight");
            highlight.setBackground(null);

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
