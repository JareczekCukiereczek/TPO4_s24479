package zad1;

import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class PublisherSample {
    public TextArea newsToTopic;
    public ComboBox<String> allTopics;
    private PublisherLogic publisherLogic;

    public void setPublisherLogic(PublisherLogic publisherLogic) throws IOException {
        this.publisherLogic = publisherLogic;
        refresh();
    }

    public void refresh() throws IOException {
        if (allTopics != null) {
            allTopics.getItems().clear();
            allTopics.getItems().addAll(publisherLogic.getAllTopics());
        }
    }
    public void refreshComboBox() throws IOException {
        allTopics.getItems().clear();
        allTopics.getItems().addAll(publisherLogic.getAllTopics());
    }

    public void addNews() throws IOException {
        if (allTopics.getValue() != null && !newsToTopic.getText().isEmpty()) {
            publisherLogic.addNewNews(allTopics.getValue(), newsToTopic.getText());
            newsToTopic.clear();
        }
    }

    public void addTopic() throws IOException {
        if (!newsToTopic.getText().isEmpty()) {
            System.out.println("Mam :" + newsToTopic.getText());
            publisherLogic.addNewTopic(newsToTopic.getText());
            clear();
        }
    }

    public void deleteTopicNews() throws IOException {
        if (!newsToTopic.getText().isEmpty()) {
            //System.out.println("Mam :"+ newsToTopic.getText());
            publisherLogic.deleteTopicNews(allTopics.getValue(), newsToTopic.getText());
            newsToTopic.clear();
        }
    }

    public void clear() throws IOException {
        refreshComboBox();
        refreshComboBox();
        refreshComboBox();
    }

    public void deleteTopic() throws IOException {
        if (allTopics.getValue() != null) {
            publisherLogic.deleteTopic(allTopics.getValue());
            clear();
        }
    }

}
