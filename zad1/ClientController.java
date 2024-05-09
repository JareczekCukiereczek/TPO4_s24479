package zad1;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class ClientController {
    public ComboBox<String> allTopics;
    public ComboBox<String> userTopics;
    public TextArea topicNews;
    public TextField username;

    private Client client;

    public void setClient(Client client) throws IOException {
        this.client = client;
        refreshAllTopics();
        refreshUserTopics();
        refreshTopicNews();
    }

    public void unsubscribe() throws IOException {
        String message = client.unSubscribe(userTopics.getValue());
        refreshUserTopics();
        refreshTopicNews();
    }

    public void subscribe() throws IOException {
        String message = client.subscribe(allTopics.getValue());
        refreshUserTopics();
        refreshTopicNews();
    }

    public void refresh() throws IOException {
        refreshAllTopics();
        refreshUserTopics();
        refreshTopicNews();
    }

    private void refreshAllTopics() throws IOException {
        List<String> topics = client.getAllTopics();
        allTopics.getItems().clear();
        allTopics.getItems().addAll(topics);
    }

    private void refreshUserTopics() throws IOException {
        List<String> userTopicsList = client.getMyTopics();
        userTopics.getItems().clear();
        userTopics.getItems().addAll(userTopicsList);
    }

    private void refreshTopicNews() throws IOException {
        StringBuilder result = new StringBuilder();
        for (String topic : userTopics.getItems()) {
            result.append(topic).append("\n");
            List<String> news = client.getNewsOnTopic(topic);
            for (String singleNews : news) {
                result.append(singleNews).append("\n");
            }
        }
        topicNews.clear();
        topicNews.setText(result.toString());
    }
}
