package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Publisher {
    private final String positionName = "publisher";
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private static final String HOST = "localhost";
    private static final int PORT = 4001;


    public Publisher() throws Exception {
        socket = new Socket(HOST, PORT);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String receiveMessage() throws IOException {
        return reader.readLine();
    }
    public void sendMessage(String mess) {
        System.out.println("Sended : name "+ mess);
        writer.println(positionName + mess);
    }



}
