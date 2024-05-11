package zad1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublisherLogic {
    private Publisher publisher;
    public String addTopic = "-addTopic-";
    public String deleteTopicNews = "-deleteNewsTopic-";
    public String addNews="-addNewsTopic-";
    public String deleteTopic = "-deleteTopic-";
    public String getAll="-getAllTopics";
    public String starter="0";

    public PublisherLogic() throws Exception {
        publisher = new Publisher();
    }

    public List<String> getTopics() throws IOException {
        publisher.sendMessage(getAll);
        String response = publisher.receiveMessage();
        List<String> topics = new ArrayList<>();

        if (response != null && response.startsWith(starter)) {
            String result = publisher.receiveMessage();
            if (result != null && !result.isEmpty()) {
                topics.addAll(parseToList(result));
            }
        }
        return topics;
    }

    public String addTopicOperation(String topic) throws IOException {
        publisher.sendMessage(addTopic + topic);
        System.out.println("Send to add topic: " + addTopic+ topic);
        return publisher.receiveMessage();
    }
    public String deleteTopicOperation(String topic) throws IOException {
        publisher.sendMessage(deleteTopic + topic);
        System.out.println("Send to delete topic: " + deleteTopic+ topic);
        return publisher.receiveMessage();
    }


    public String deleteTopicNews(String topic, String news) throws IOException {
        publisher.sendMessage(deleteTopicNews + topic + "-" + news);
        System.out.println("Send to delete news: " + deleteTopicNews+ topic);
        return publisher.receiveMessage();
    }

    public String addNewNews(String topic, String news) throws IOException {
        publisher.sendMessage(addNews + topic + "-" + news);
        System.out.println("Send to add news: " + addNews+ topic);
        return publisher.receiveMessage();
    }

    private List<String> parseToList(String response) {
        return List.of(response.split("-"));
    }


}
