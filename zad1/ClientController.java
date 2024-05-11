package zad1;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class ClientController {
    public TextArea topicNews;
    public ComboBox<String> userTopic;//zm
    public TextField user;
    public ComboBox<String> allTopic;
    private Client client;

    public void setClient(Client client){
        this.client = client;
        refreshAllTopics();
        refreshUserTopics();
        refreshTopicNews();
    }

    public void subscribeCurrTopic(){
        client.subscribeTopic(allTopic.getValue());
        refreshUserTopics();
        refreshTopicNews();
    }
    public void unsubscribeCurrTopic(){
        client.unsubscribeTopic(userTopic.getValue());
        refreshUserTopics();
        refreshTopicNews();
    }

    public void refreshAll(){
        refreshAllTopics();
        refreshUserTopics();
        refreshTopicNews();
    }
    private void refreshUserTopics(){
        List<String> userTopicsList = client.getMyListOfTopic();
        userTopic.getItems().clear();
        userTopic.getItems().addAll(userTopicsList);
    }

    private void refreshAllTopics(){
        List<String> topics = client.getAllTopics();
        allTopic.getItems().clear();
        allTopic.getItems().addAll(topics);
    }


    private void refreshTopicNews(){
        StringBuilder result = new StringBuilder();
        for (String topic : userTopic.getItems()) {
            result.append(topic).append("\n");
            List<String> news = null;
            try {
                news = client.getNewsTopic(topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String singleNews : news) {
                result.append(singleNews).append("\n");
            }
        }
        topicNews.clear();
        topicNews.setText(result.toString());
    }
}
