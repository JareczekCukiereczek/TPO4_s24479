package zad1;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class PublisherGUIOperation {
    private PublisherLogic publisherLogic;
    public TextArea newsToCurrentTopic;
    public ComboBox<String> allTopics;

    public void setPublisherLogic(PublisherLogic publisherLogic) throws IOException {
        this.publisherLogic = publisherLogic;
        refresh();
    }

    public void refreshComboBox() throws IOException {
        allTopics.getItems().clear();
        allTopics.getItems().addAll(publisherLogic.getTopics());
    }

    public void newsAdding() {
        if (allTopics.getValue() != null && !newsToCurrentTopic.getText().isEmpty()) {
            try {
                publisherLogic.addNewNews(allTopics.getValue(), newsToCurrentTopic.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            newsToCurrentTopic.clear();
        }
    }

    public void topicAdding()  {
        if (!newsToCurrentTopic.getText().isEmpty()) {
            System.out.println("Added :" + newsToCurrentTopic.getText());
            try {
                publisherLogic.addTopicOperation(newsToCurrentTopic.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            newsToCurrentTopic.clear();
            try {
                clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void newsDeleting()  {
        if (!newsToCurrentTopic.getText().isEmpty()) {
            try {
                publisherLogic.deleteTopicNews(allTopics.getValue(), newsToCurrentTopic.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            newsToCurrentTopic.clear();
        }
    }

    public void clear() throws IOException {
        refreshComboBox();
        refreshComboBox();
        refreshComboBox();
    }

    public void topicDelete()  {
        if (allTopics.getValue() != null) {
            try {
                publisherLogic.deleteTopicOperation(allTopics.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void refresh() throws IOException {
        if (allTopics != null) {
            allTopics.getItems().clear();
            allTopics.getItems().addAll(publisherLogic.getTopics());
        }
    }

}
