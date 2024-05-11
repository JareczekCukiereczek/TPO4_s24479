package zad1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client extends Application {
    private String name;
    private Socket socket;
    private PrintWriter printWriter = null;
    private BufferedReader reader = null;
    private String starter="0";
    // private List<ClientThread> clientThreads = new ArrayList<>();
    public String getAllTopics="-getAllTopics";
    public String getMyTopics="-getMyTopics";
    public String subs="-subscribe-";
    public String unsub="-unsubscribe-";
    public String getNews="-newsOnTopic-";


    public Client() {
        this.name = "Client";
        try {
            socket = new Socket("localhost", 4001);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //rozroznianie klientow
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
            Parent root = fxmlLoader.load();
            ClientController controller = fxmlLoader.getController();
            if (this.name == null) {
                this.name = "Client";
            }
            controller.setClient(this);
            primaryStage.setTitle("Client s24479");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //obsługa multiclient
    /*
    public void connectToServer(String host, int port, String name) {
        try {
            Socket socket = new Socket(host, port);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientThread clientThread = new ClientThread(name, socket, printWriter, reader);
            clientThreads.add(clientThread);
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    public List<String> getAllTopics()  {
        printWriter.println(this.name + getAllTopics);
        String response = null;
        try {
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean correct = response.startsWith(starter);

        if (correct) {
            String topicsString = null;
            try {
                topicsString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringToList(topicsString);
        } else {
            return Collections.emptyList();
        }
    }
    public List<String> getMyListOfTopic()  {
        printWriter.println(this.name + getMyTopics);
        String response = null;
        try {
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null || response.isEmpty()) {
            return new ArrayList<>();
        }

        boolean correct = response.startsWith(starter);
        String result = "ERROR";
        if (correct) {
            try {
                result = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return stringToList(result);
        }
    }


    public String subscribeTopic(String topic)  {
        printWriter.println(this.name + subs + topic);
        String response = null;
        try {
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response);
        if (response.startsWith(starter)) {
            try {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "ERROR";
        }
        return response;
    }

    public String unsubscribeTopic(String topic) {
        printWriter.println(this.name + unsub + topic);
        String response = null;
        try {
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean correct = response.startsWith(starter);

        if (correct) {
            try {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "ERROR";
        }
        return response;
    }


    public List<String> getNewsTopic(String topic) throws IOException {
        printWriter.println(this.name + getNews + topic);
        String response = reader.readLine();

        if (response == null || response.isEmpty()) {
            return new ArrayList<>();
        }

        boolean correct = response.startsWith(starter);
        String result;
        if (correct) {
            result = reader.readLine();
        } else {
            result = "ERROR";
        }

        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return stringToList(result);
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private List<String> stringToList(String response) {
        return Arrays.asList(response.split("-"));
    }
}
