package zad1;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    private List<String> topics = new ArrayList<>();
    private Map<String, List<String>> topicNews = new HashMap<>();
    private Map<String, List<String>> clientsTopics = new HashMap<>();
    private ServerSocketChannel socketChannel = null;
    private Selector selector = null;
    public String topicsPath="zad1/data/topics.txt";
    public String newsPath="zad1/data/news.txt";
    public String topicsClientsPath="zad1/data/topicsClients.txt";

    public Server(String host, int port) {
        initializeTopics();
        initializeNews();
        initializeClientsTopics();
        startServer(host, port);
        System.out.println("Server ready for connections " + port);
        srvConnections();
    }

    private void initializeTopics() {
        loadTopicsFromFile(topicsPath, topics);
        topics.forEach(System.out::println);
    }

    private void initializeNews() {
        loadNewsFromFile(newsPath, topicNews);
        topicNews.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void initializeClientsTopics() {
        loadClientTopicsFromFile(topicsClientsPath, clientsTopics);
        clientsTopics.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void startServer(String host, int port) {
        try {
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(host, port));
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void srvConnections() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        System.out.println("Connections accepted");
                        SocketChannel socketChannel = this.socketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        continue;
                    }
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        serviceRequest(socketChannel);
                        continue;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private static Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private StringBuffer request = new StringBuffer();

    private void serviceRequest(SocketChannel socketChannel) {
        if (!socketChannel.isOpen()) {
            return;
        }
        request.setLength(0);
        byteBuffer.clear();

        try {
            readLoop:
            while (true) {
                int i = socketChannel.read(byteBuffer);
                if (i > 0) {
                    byteBuffer.flip();
                    CharBuffer cbuf = charset.decode(byteBuffer);
                    while (cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\n' || c == '\r') {
                            break readLoop;
                        } else {
                            request.append(c);
                        }
                    }
                }
            }
            String[] req = request.toString().split(":");
            String client = req[0];
            String cmd = req[1];


            switch (client) {
                case "publisher":
                    switch (cmd) {
                        case "getAllTopics":
                            sendResponse(socketChannel, 0, listToString(topics));
                            break;
                        case "getAllNewsTopics":
                            if (req.length != 3) {
                                sendResponse(socketChannel, 1, null);
                            } else {
                                sendResponse(socketChannel, 0, listToString(topicNews.get(req[2])));
                            }
                            break;
                        case "addTopic":
                            if (req.length != 3) {
                                sendResponse(socketChannel, 1, null);
                                System.out.println("Jestem w addTopic1");
                            } else {
                                topics.add(req[2]);
                                System.out.println("Jestem w addTopic2");
                                saveListToFile(topicsPath, topics);
                                sendResponse(socketChannel, 0, " " + req[2]);
                            }
                            break;
                        case "deleteTopic":
                            if (req.length != 3) {
                                sendResponse(socketChannel, 1, null);
                            } else if (topicNews.containsKey(req[2]) && topicNews.get(req[2]).size() != 0) {
                                System.out.println("Jestem w deleteTopic1");
                                sendResponse(socketChannel, 0, "");
                            } else {
                                topics.remove(req[2]);
                                System.out.println("Usuwam topic");
                                saveListToFile(topicsPath, topics);
                                sendResponse(socketChannel, 0, "" + req[2]);
                            }
                            break;
                        case "addNewsTopic":
                            if (req.length != 4) {
                                sendResponse(socketChannel, 1, null);
                            } else {
                                topicNews.computeIfAbsent(req[2], k -> new ArrayList<>()).add(req[3]);
                                saveMapToFile(newsPath, topicNews);
                                sendResponse(socketChannel, 0, " " + req[2]);
                            }
                            break;
                        case "deleteNewsTopic":
                            if (req.length != 4) {
                                sendResponse(socketChannel, 1, null);
                                System.out.println("Jestem w deleteNewsTopic1");
                            } else {
                                System.out.println("Jestem w deleteNewsTopic2");
                                String topic = req[2];
                                String newsToDelete = req[3];
                                if (topicNews.containsKey(topic) && topicNews.get(topic).contains(newsToDelete)) {
                                    topicNews.get(topic).remove(newsToDelete);
                                    saveMapToFile(newsPath, topicNews);
                                    sendResponse(socketChannel, 0, "Usunięto wiadomość z tematu " + topic);
                                } else {
                                    System.out.println("Podana wiadomość nie istnieje w temacie");
                                    sendResponse(socketChannel, 1, "Podana wiadomość nie istnieje w temacie " + topic);
                                }
                            }
                            break;
                    }
                    break;

                default:
                    switch (cmd) {
                        case "bye":
                            sendResponse(socketChannel, 0, null);
                            socketChannel.close();
                            socketChannel.socket().close();
                            break;
                        case "subscribe":
                            if (req.length != 3) {
                                sendResponse(socketChannel, 1, null);
                            } else {
                                clientsTopics.computeIfAbsent(client, k -> new ArrayList<>()).add(req[2]);
                                sendResponse(socketChannel, 0, "subscribe");
                                saveMapToFile(topicsClientsPath, clientsTopics);
                            }
                            break;
                        case "unsubscribe":
                            if (req.length != 3) {
                                sendResponse(socketChannel, 1, null);
                            } else {
                                clientsTopics.get(client).remove(req[2]);
                                sendResponse(socketChannel, 0, "unsubscribe");
                            }
                            break;
                        case "getAllTopics":
                            sendResponse(socketChannel, 0, listToString(topics));
                            break;
                        case "getMyTopics":
                            if (!clientsTopics.containsKey(client)) {
                                sendResponse(socketChannel, 0, "");
                            } else {
                                sendResponse(socketChannel, 0, listToString(clientsTopics.get(client)));
                            }
                            break;
                        case "newsOnTopic":
                            if (req.length != 3) {
                                sendResponse(socketChannel, 1, null);
                            } else {
                                if (!topicNews.containsKey(req[2])) {
                                    sendResponse(socketChannel, 0, "");
                                } else {
                                    sendResponse(socketChannel, 0, listToString(topicNews.get(req[2])));
                                }
                            }
                            break;
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuffer response = new StringBuffer();

    private void sendResponse(SocketChannel sc, int rc, String addMsg) throws IOException {
        response.setLength(0);
        response.append(rc);
        response.append("\n");
        if (addMsg != null) {
            response.append(addMsg);
            response.append("\n");
        }
        ByteBuffer byteBuffer = charset.encode(CharBuffer.wrap(response));
        sc.write(byteBuffer);
    }

    public void saveToFile(String path, String textToSave) {
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(textToSave);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try{
                if(bw!=null)
                    bw.close();
            }catch(Exception ex){

            }
        }
    }

    private void loadTopicsFromFile(String path, List<String> list) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNewsFromFile(String path, Map<String, List<String>> map) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] mapArray = line.split(":");
                List<String> helperList = new ArrayList<>();
                for (int i = 1; i < mapArray.length; i++) {
                    helperList.add(mapArray[i]);
                }
                map.put(mapArray[0], helperList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClientTopicsFromFile(String path, Map<String, List<String>> map) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] mapArray = line.split(":");
                List<String> helperList = new ArrayList<>();
                for (int i = 1; i < mapArray.length; i++) {
                    helperList.add(mapArray[i]);
                }
                map.put(mapArray[0], helperList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveListToFile(String path, List<String> list) {
        StringBuilder toSave = new StringBuilder();
        for (String item : list) {
            toSave.append(item).append("\n");
        }
        saveToFile(path, toSave.toString());
    }

    private void saveMapToFile(String path, Map<String, List<String>> map) {
        StringBuilder toSave = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            toSave.append(entry.getKey());
            for (String value : entry.getValue()) {
                toSave.append(":").append(value);
            }
            toSave.append("\n");
        }
        saveToFile(path, toSave.toString());
    }

    public static void main(String[] args) {
        new Server("localhost", 5001);
    }

    public static String listToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        for (String s : list) {
            result.append(s).append(":");
        }
        return result.toString();
    }
}
