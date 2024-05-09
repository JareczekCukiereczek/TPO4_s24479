package zad1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublisherLogic {
    private Publisher publisher;
    public String addTopic = ":addTopic:";
    public String deleteTopicNews = ":deleteNewsTopic:";
    public String addNews=":addNewsTopic:";
    public String deleteTopic = ":deleteTopic:";

    public PublisherLogic() throws Exception {
        publisher = new Publisher();
    }

    public List<String> getAllTopics() throws IOException {
        publisher.sendMessage(":getAllTopics");
        String response = publisher.receiveMessage();
        List<String> topics = new ArrayList<>();

        if (response != null && response.startsWith("0")) {
            String result = publisher.receiveMessage();
            if (result != null && !result.isEmpty()) {
                topics.addAll(stringToList(result));
            }
        }
        return topics;
    }

    public String addNewTopic(String topic) throws IOException {
        publisher.sendMessage(addTopic + topic);
        return publisher.receiveMessage();
    }
    public String deleteTopic(String topic) throws IOException {
        publisher.sendMessage(deleteTopic + topic);
        return publisher.receiveMessage();
    }


    public String deleteTopicNews(String topic, String news) throws IOException {
        publisher.sendMessage(deleteTopicNews + topic + ":" + news);
        //System.out.println("Wysłałem " + deleteTopic+ topic);
        return publisher.receiveMessage();
    }

    public String addNewNews(String topic, String news) throws IOException {
        publisher.sendMessage(addNews + topic + ":" + news);
        return publisher.receiveMessage();
    }

    private List<String> stringToList(String response) {
        return List.of(response.split(":"));
    }


}
