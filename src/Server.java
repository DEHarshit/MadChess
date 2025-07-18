import javax.swing.*;
import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean isRunning = false;

    private Thread acceptThread;

    public void start(int port, Game game, JFrame menu) throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;

        acceptThread = new Thread(() -> {
            try {
                System.out.println("Server started. Waiting for client...");
                clientSocket = serverSocket.accept(); // Will block here
                System.out.println("Client connected.");

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                SwingUtilities.invokeLater(() -> {
                    menu.setVisible(false);
                    game.beginGame("Player1",menu,game);
                });

                String move;
                while ((move = in.readLine()) != null) {
                    System.out.println("Received: " + move);
                    String[] parts = move.split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int dx = Integer.parseInt(parts[2]);
                    int dy = Integer.parseInt(parts[3]);
                    int piece = Integer.parseInt(parts[4]);
                    game.makeMove(x, y, dx, dy, "Player1", piece,true);
                }
            } catch (IOException e) {
                if (isRunning) {
                    e.printStackTrace();
                } else {
                    System.out.println("Server stopped before client connected.");
                }
            }
        });
        acceptThread.start();
    }

    public void sendMove(int x, int y, int dx, int dy, int piece) {
        if (out != null) {
            out.println(x + "," + y + "," + dx + "," + dy + "," + piece);
        }
    }

    public void stop(JFrame menu) throws IOException {
        isRunning = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }

        if (in != null) in.close();
        if (out != null) out.close();

        if (menu != null) {
            SwingUtilities.invokeLater(() -> menu.setVisible(true));
        }

        System.out.println("Server stopped.");
    }
}
