package zad1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class PublisherGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("publisher.fxml"));
            Parent root = fxmlLoader.load();
            PublisherGUIOperation controller = fxmlLoader.getController();
            controller.setPublisherLogic(new PublisherLogic());
            primaryStage.setTitle("Publisher s24479");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
