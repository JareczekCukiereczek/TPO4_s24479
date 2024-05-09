package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Publisher {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private final String name = "publisher";
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Publisher() throws Exception {
        socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        System.out.println("Wysłałem : name "+ message);
        out.println(name + message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

}
