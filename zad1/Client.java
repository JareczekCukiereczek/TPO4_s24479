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
    private Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String name;

    public Client() {
        this.name = "client";
        try {
            socket = new Socket("localhost", 5001);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String subscribe(String topic) throws IOException {
        out.println(this.name + ":subscribe:" + topic);
        String response = in.readLine();
        System.out.println(response);

        if (response.startsWith("0")) {
            return in.readLine();
        } else {
            return "Błąd wystąpił";
        }
    }

    public String unSubscribe(String topic) throws IOException {
        out.println(this.name + ":unsubscribe:" + topic);
        String response = in.readLine();
        boolean ok = response.startsWith("0");

        if (ok) {
            return in.readLine();
        } else {
            return "Błąd wystąpił";
        }
    }

    public List<String> getAllTopics() throws IOException {
        out.println(this.name + ":getAllTopics");
        String response = in.readLine();
        boolean ok = response.startsWith("0");

        if (ok) {
            String topicsString = in.readLine();
            return stringToList(topicsString);
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> getMyTopics() throws IOException {
        out.println(this.name + ":getMyTopics");
        String response = in.readLine();

        if (response == null || response.isEmpty()) {
            return new ArrayList<>();
        }

        boolean ok = response.startsWith("0");
        String result = ok ? in.readLine() : "Błąd wystąpił";

        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return stringToList(result);
        }
    }

    public List<String> getNewsOnTopic(String topic) throws IOException {
        out.println(this.name + ":newsOnTopic:" + topic);
        String response = in.readLine();

        if (response == null || response.isEmpty()) {
            return new ArrayList<>();
        }

        boolean ok = response.startsWith("0");
        String result = ok ? in.readLine() : "Błąd wystąpił";

        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return stringToList(result);
        }
    }

    private List<String> stringToList(String response) {
        String[] splitted = response.split(":");
        return new ArrayList<String>(Arrays.asList(splitted));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent root = fxmlLoader.load();

            ClientController controller = fxmlLoader.getController();
            if (this.name == null) {
                this.name = "client";
            }
            controller.setClient(this);

            primaryStage.setTitle("Klient");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
