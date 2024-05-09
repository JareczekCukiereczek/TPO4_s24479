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


    public Client() {
        this.name = "client";
        try {
            socket = new Socket("localhost", 5001);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getAllTopics() throws IOException {
        printWriter.println(this.name + ":getAllTopics");
        String response = reader.readLine();
        boolean correct = response.startsWith("0");

        if (correct) {
            String topicsString = reader.readLine();
            return stringToList(topicsString);
        } else {
            return Collections.emptyList();
        }
    }
    public List<String> getMyTopics() throws IOException {
        printWriter.println(this.name + ":getMyTopics");
        String response = reader.readLine();

        if (response == null || response.isEmpty()) {
            return new ArrayList<>();
        }

        boolean correct = response.startsWith("0");
        String result = correct ? reader.readLine() : "Błąd wystąpił";

        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return stringToList(result);
        }
    }

    public String subscribeTopic(String topic) throws IOException {
        printWriter.println(this.name + ":subscribe:" + topic);
        String response = reader.readLine();
        System.out.println(response);

        if (response.startsWith("0")) {
            return reader.readLine();
        } else {
            return "Błąd wystąpił";
        }
    }

    public String unsubscribeTopic(String topic) throws IOException {
        printWriter.println(this.name + ":unsubscribe:" + topic);
        String response = reader.readLine();
        boolean correct = response.startsWith("0");

        if (correct) {
            return reader.readLine();
        } else {
            return "ERROR";
        }
    }


    public List<String> getNewsTopic(String topic) throws IOException {
        printWriter.println(this.name + ":newsOnTopic:" + topic);
        String response = reader.readLine();

        if (response == null || response.isEmpty()) {
            return new ArrayList<>();
        }

        boolean correct = response.startsWith("0");
        String result = correct ? reader.readLine() : "ERROR";

        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return stringToList(result);
        }
    }

    private List<String> stringToList(String response) {
        String[] split = response.split(":");
        return new ArrayList<String>(Arrays.asList(split));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
            Parent root = fxmlLoader.load();

            ClientController controller = fxmlLoader.getController();
            if (this.name == null) {
                this.name = "client";
            }
            controller.setClient(this);

            primaryStage.setTitle("Klient s24479");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
