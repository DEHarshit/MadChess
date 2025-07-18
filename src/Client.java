import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void start(String ip, int port, Game game,JFrame menu) throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connected to server.");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        menu.setVisible(false);
        game.beginGame("Player2",menu,game);

        new Thread(() -> {
            String move;
            try {
                while ((move = in.readLine()) != null) {
                    System.out.println("Received: " + move);
                    String[] parts = move.split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int dx = Integer.parseInt(parts[2]);
                    int dy = Integer.parseInt(parts[3]);
                    int piece = Integer.parseInt(parts[4]);
                    game.makeMove(x, y, dx, dy, "Player2",piece,false); // opponent move
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMove(int x, int y, int dx, int dy,int piece) {
        out.println(x + "," + y + "," + dx + "," + dy + "," + piece);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
