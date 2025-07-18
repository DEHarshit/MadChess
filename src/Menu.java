import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;

public class Menu {
    JFrame menu = new JFrame();

    private final JLabel menuBackground = new JLabel();
    private final JLabel hostBackground = new JLabel();
    private final JLabel joinBackground = new JLabel();

    private final JLayeredPane mainMenu = new JLayeredPane();
    private final JLayeredPane hostMenu = new JLayeredPane();
    private final JLayeredPane joinMenu = new JLayeredPane();

    private final JLabel hostIPLabel = new JLabel();
    private final JTextField ipInputField = new JTextField("Enter IP");

    private final JButton startServer = new JButton("Start Server");
    private final JButton stopServer = new JButton("Stop Server");
    private final JButton connectButton = new JButton("Connect");

    private final JButton hostButton = new JButton("Host Game");
    private final JButton joinButton = new JButton("Join Game");

    private final JButton backHost = new JButton("Back");
    private final JButton backJoin = new JButton("Back");

    private Image originalBackgroundImage;
    private Image hostImage;
    private Image joinImage;

    private String ipAddress = "Enter the IP";

    private Server server;
    private Game game;

    public Menu() {
        originalBackgroundImage = new ImageIcon(Objects.requireNonNull(Menu.class.getResource("/assets/Background.jpg"))).getImage();
        hostImage = new ImageIcon(Objects.requireNonNull(Menu.class.getResource("/assets/HostPage.jpg"))).getImage();
        joinImage = new ImageIcon(Objects.requireNonNull(Menu.class.getResource("/assets/JoinPage.jpg"))).getImage();

        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setSize(new Dimension(1280, 720));
        menu.setMinimumSize(new Dimension(1280, 720));
        menu.setTitle("MadChess");
        menu.setIconImage(new ImageIcon(Objects.requireNonNull(Menu.class.getResource("/assets/Logo.png"))).getImage());

        mainMenu.setLayout(null);
        menuBackground.setOpaque(true);
        mainMenu.add(menuBackground, JLayeredPane.DEFAULT_LAYER);
        createButton(hostButton);
        createButton(joinButton);
        hostButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openHostPage();
            }
        });
        joinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openJoinPage();
            }
        });
        mainMenu.add(hostButton, JLayeredPane.PALETTE_LAYER);
        mainMenu.add(joinButton, JLayeredPane.PALETTE_LAYER);
        mainMenu.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeMenuComponents();
            }
        });

        hostMenu.setLayout(null);
        hostBackground.setOpaque(true);
        hostMenu.add(hostBackground, JLayeredPane.DEFAULT_LAYER);
        createButton(backHost);
        backHost.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                returnToMainMenu();
            }
        });
        hostMenu.add(backHost, JLayeredPane.PALETTE_LAYER);
        hostMenu.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeHostComponents();
            }
        });

        createButton(startServer);
        createButton(stopServer);
        hostMenu.add(startServer, JLayeredPane.PALETTE_LAYER);
        hostMenu.add(stopServer, JLayeredPane.PALETTE_LAYER);

        hostIPLabel.setForeground(Color.WHITE);
        hostIPLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        hostMenu.add(hostIPLabel, JLayeredPane.PALETTE_LAYER);

        startServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                server = new Server();
                game = new Game("Player1", "Player2");
                game.setServer(server);
                try {
                    server.start(1200, game, menu);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        stopServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (server != null) {
                    try {
                        server.stop(menu);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        joinMenu.setLayout(null);
        joinBackground.setOpaque(true);
        joinMenu.add(joinBackground, JLayeredPane.DEFAULT_LAYER);
        createButton(backJoin);
        backJoin.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                returnToMainMenu();
            }
        });
        joinMenu.add(backJoin, JLayeredPane.PALETTE_LAYER);
        joinMenu.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeJoinComponents();
            }
        });

        createButton(connectButton);
        joinMenu.add(ipInputField, JLayeredPane.PALETTE_LAYER);
        joinMenu.add(connectButton, JLayeredPane.PALETTE_LAYER);

        connectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ipAddress = ipInputField.getText();
                new Thread(() -> {
                    try {
                        Client client = new Client();
                        game = new Game("Player1", "Player2");
                        game.setClient(client);
                        client.start(ipAddress, 1200, game, menu);
                    } catch (IOException ex) {
                        //SwingUtilities.invokeLater(() -> {
                       //     JOptionPane.showMessageDialog(menu, "Failed to connect to server.\nIs it running at " + ipAddress + "?", "Connection Error", JOptionPane.ERROR_MESSAGE);
                        //});
                    }
                }).start();

            }
        });

        menu.add(mainMenu);
        menu.revalidate();
        menu.repaint();
    }

    private void openHostPage() {
        hostButton.setBackground(Color.BLACK);
        menu.remove(mainMenu);
        menu.add(hostMenu);
        menu.revalidate();
        menu.repaint();
        resizeHostComponents();

        try {
            String localIP = java.net.InetAddress.getLocalHost().getHostAddress();
            hostIPLabel.setText("Running on IP: " + localIP);
            hostIPLabel.setForeground(Color.RED);

        } catch (Exception e) {
            hostIPLabel.setText("Unable to fetch IP");
        }
    }

    private void openJoinPage() {
        joinButton.setBackground(Color.BLACK);
        menu.remove(mainMenu);
        menu.add(joinMenu);
        menu.revalidate();
        menu.repaint();
        resizeJoinComponents();
    }

    private void returnToMainMenu() {
        backHost.setBackground(Color.BLACK);
        backJoin.setBackground(Color.BLACK);
        menu.remove(hostMenu);
        menu.remove(joinMenu);
        menu.add(mainMenu);
        menu.revalidate();
        menu.repaint();
        resizeMenuComponents();
    }

    private void resizeMenuComponents() {
        int width = mainMenu.getWidth();
        int height = mainMenu.getHeight();
        if (width == 0 || height == 0) return;

        Image scaled = originalBackgroundImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        menuBackground.setIcon(new ImageIcon(scaled));
        menuBackground.setBounds(0, 0, width, height);

        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = width / 2 - buttonWidth / 2;
        int startY = height / 2 - 80;

        hostButton.setBounds(centerX, startY, buttonWidth, buttonHeight);
        joinButton.setBounds(centerX, startY + 70, buttonWidth, buttonHeight);
    }

    private void resizeHostComponents() {
        int width = hostMenu.getWidth();
        int height = hostMenu.getHeight();
        if (width == 0 || height == 0) return;

        Image scaled = hostImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        hostBackground.setIcon(new ImageIcon(scaled));
        hostBackground.setBounds(0, 0, width, height);

        int buttonWidth = 120;
        int buttonHeight = 40;
        backHost.setBounds(20, 20, buttonWidth, buttonHeight);

        hostIPLabel.setBounds(20, 70, 400, 30);
        startServer.setBounds(20, 110, 160, 40);
        stopServer.setBounds(190, 110, 160, 40);

    }

    private void resizeJoinComponents() {
        int width = joinMenu.getWidth();
        int height = joinMenu.getHeight();
        if (width == 0 || height == 0) return;

        Image scaled = joinImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        joinBackground.setIcon(new ImageIcon(scaled));
        joinBackground.setBounds(0, 0, width, height);

        int buttonWidth = 120;
        int buttonHeight = 40;
        backJoin.setBounds(20, 20, buttonWidth, buttonHeight);

        ipInputField.setBounds(20, 70, 300, 40);
        connectButton.setBounds(330, 70, 120, 40);

    }

    private void createButton(JButton button) {
        button.setFont(new Font("Helvetica", Font.BOLD, 18));
        button.setBackground(new Color(0, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setPressedIcon(null);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY); // On hover
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK); // On exit
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(Color.GRAY); // On press
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(Color.BLACK); // Reset
            }
        });

    }

}
