package zad1;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class PublisherGUIOperation {
    public TextArea newsToCurrentTopic;
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
        if (allTopics.getValue() != null && !newsToCurrentTopic.getText().isEmpty()) {
            publisherLogic.addNewNews(allTopics.getValue(), newsToCurrentTopic.getText());
            newsToCurrentTopic.clear();
        }
    }

    public void addTopic() throws IOException {
        if (!newsToCurrentTopic.getText().isEmpty()) {
            System.out.println("Mam :" + newsToCurrentTopic.getText());
            publisherLogic.addTopic(newsToCurrentTopic.getText());
            clear();
        }
    }

    public void deleteTopicNews() throws IOException {
        if (!newsToCurrentTopic.getText().isEmpty()) {
            //System.out.println("Mam :"+ newsToTopic.getText());
            publisherLogic.deleteTopicNews(allTopics.getValue(), newsToCurrentTopic.getText());
            newsToCurrentTopic.clear();
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
