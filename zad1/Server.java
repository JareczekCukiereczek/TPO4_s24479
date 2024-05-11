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
    private Map<String, List<String>> topicsClients = new HashMap<>();
    private List<String> topics = new ArrayList<>();
    private Map<String, List<String>> topicNews = new HashMap<>();
    private ServerSocketChannel socketChannel = null;
    private Selector selector = null;
    public String topicsPath="zad1/data/topics.txt";
    public String newsPath="zad1/data/news.txt";
    public String topicsClientsPath="zad1/data/topicsClients.txt";
    //obslsuga requestu
    private static Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private StringBuffer request = new StringBuffer();
    private StringBuffer messageRespon = new StringBuffer();

    public Server(String hostname, int port) {
        initializeTopics();
        initializeNews();
        initializeClientsTopics();
        startServer(hostname, port);
        System.out.println("Server ready for connections");
        srvConnections();
    }

    private void initializeTopics() {
        loaderTopicsFile(topicsPath, topics);
        topics.forEach(System.out::println);
    }

    private void initializeNews() {
        loaderNewsFile(newsPath, topicNews);
        topicNews.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void initializeClientsTopics() {
        loadClientTopicsFromFile(topicsClientsPath, topicsClients);
        topicsClients.forEach((key, value) -> System.out.println(key + " " + value));
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
    //obsługa multiclient
/*
    private void startServer(String host, int port) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientSocket = serverSocketChannel.accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);
                        System.out.println("New client connected");
                    } else if (key.isReadable()) {
                        SocketChannel clientSocket = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int bytesRead = clientSocket.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            CharBuffer charBuffer = charset.decode(buffer);
                            String request = charBuffer.toString();
                            new Thread(() -> serviceRequest(clientSocket, request)).start();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
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
                    //czytanie
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        requestProccesor(socketChannel);
                        continue;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
    //obsluga requestow
    private void requestProccesor(SocketChannel socketChannel) {
        if (!socketChannel.isOpen())
        {
            return;
        }
        request.setLength(0);
        byteBuffer.clear();

        try
        {
            readLoop:
            while (true)
            {
                int i = socketChannel.read(byteBuffer);
                if (i > 0)
                {
                    byteBuffer.flip();
                    CharBuffer charBuffer = charset.decode(byteBuffer);
                    while (charBuffer.hasRemaining())
                    {
                        char chars = charBuffer.get();
                        if (chars == '\n' || chars == '\r')
                        {
                            break readLoop;
                        }
                        else
                        {
                            request.append(chars);
                        }
                    }
                }
            }
            String[] requestToParse = request.toString().split("-");
            String client = requestToParse[0];
            String command = requestToParse[1];



            switch (client) {
                case "publisher":
                    switch (command) {
                        case "getAllTopics":
                            responseSender(socketChannel, 0, listToStringFormat(topics));
                            break;
                        case "getAllNewsTopics":
                            if (requestToParse.length != 3) {
                                responseSender(socketChannel, 1, null);
                            } else {
                                responseSender(socketChannel, 0, listToStringFormat(topicNews.get(requestToParse[2])));
                            }
                            break;
                        case "addTopic":
                            if (requestToParse.length != 3) {
                                responseSender(socketChannel, 1, null);
                                System.out.println("Adding topic");
                            } else {
                                topics.add(requestToParse[2]);
                                System.out.println("Adding topic");
                                //MapSaveFile(topicsClientsPath, topicsClients);
                                ListSaveFile(topicsPath,topics);
                                responseSender(socketChannel, 0, " " + requestToParse[2]);
                            }
                            break;
                        case "deleteTopic":
                            if (requestToParse.length != 3) {
                                responseSender(socketChannel, 1, null);
                            } else if (topicNews.containsKey(requestToParse[2]) && topicNews.get(requestToParse[2]).size() != 0) {
                                System.out.println("Deleting topic");
                                responseSender(socketChannel, 0, "");
                            } else {
                                topics.remove(requestToParse[2]);
                                System.out.println("Deleting topic");
                                //MapSaveFile(topicsClientsPath, topicsClients);
                                ListSaveFile(topicsPath, topics);
                                responseSender(socketChannel, 0, "" + requestToParse[2]);
                            }
                            break;
                        case "addNewsTopic":
                            if (requestToParse.length != 4) {
                                responseSender(socketChannel, 1, null);
                            } else {
                                topicNews.computeIfAbsent(requestToParse[2], k -> new ArrayList<>()).add(requestToParse[3]);
                                MapSaveFile(newsPath, topicNews);
                                responseSender(socketChannel, 0, " " + requestToParse[2]);
                            }
                            break;
                        case "deleteNewsTopic":
                            if (requestToParse.length != 4) {
                                responseSender(socketChannel, 1, null);
                            } else {
                                System.out.println("Deleting  deleteNewsTopic");
                                String topic = requestToParse[2];
                                String newsToDelete = requestToParse[3];
                                if (topicNews.containsKey(topic) && topicNews.get(topic).contains(newsToDelete)) {
                                    topicNews.get(topic).remove(newsToDelete);
                                    MapSaveFile(newsPath, topicNews);
                                    responseSender(socketChannel, 0, "Deleted topic from " + topic);
                                } else {
                                    System.out.println("News doesn't exists");
                                    responseSender(socketChannel, 1, "News doesn't exists in topic " + topic);
                                }
                            }
                            break;
                    }
                    break;

                default:
                    switch (command) {
                        case "subscribe":
                            if (requestToParse.length != 3) {
                                responseSender(socketChannel, 1, null);
                            } else {
                                topicsClients.computeIfAbsent(client, k -> new ArrayList<>()).add(requestToParse[2]);
                                responseSender(socketChannel, 0, "subscribe");
                                MapSaveFile(topicsClientsPath, topicsClients);
                            }
                            break;
                        case "unsubscribe":
                            if (requestToParse.length != 3) {
                                responseSender(socketChannel, 1, null);
                            } else {
                                topicsClients.get(client).remove(requestToParse[2]);
                                MapSaveFile(topicsClientsPath, topicsClients);
                                responseSender(socketChannel, 0, "unsubscribe");
                            }
                            break;
                        case "getAllTopics":
                            responseSender(socketChannel, 0, listToStringFormat(topics));
                            break;
                        case "getMyTopics":
                            if (!topicsClients.containsKey(client)) {
                                responseSender(socketChannel, 0, "");
                            } else {
                                responseSender(socketChannel, 0, listToStringFormat(topicsClients.get(client)));
                            }
                            break;
                        case "newsOnTopic":
                            if (requestToParse.length != 3) {
                                responseSender(socketChannel, 1, null);
                            } else {
                                if (!topicNews.containsKey(requestToParse[2])) {
                                    responseSender(socketChannel, 0, "");
                                } else {
                                    responseSender(socketChannel, 0, listToStringFormat(topicNews.get(requestToParse[2])));
                                }
                            }
                            break;
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(String path, String textToSave) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {
            bufferedWriter.write(textToSave);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private void loaderNewsFile(String path, Map<String, List<String>> map) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] mapArray = line.split("-");
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

    private void loaderTopicsFile(String paths, List<String> list) {
        try (BufferedReader reader = new BufferedReader(new FileReader(paths))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void loadClientTopicsFromFile(String path, Map<String, List<String>> map) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] mapArray = line.split("-");
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

    private void ListSaveFile(String path, List<String> list) {
        StringBuilder toSave = new StringBuilder();
        for (String item : list) {
            toSave.append(item).append("\n");
        }
        saveFile(path, toSave.toString());
    }

    private void MapSaveFile(String path, Map<String, List<String>> map) {
        StringBuilder saver = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            saver.append(entry.getKey());
            for (String value : entry.getValue()) {
                saver.append("-").append(value);
            }
            saver.append("\n");
        }
        saveFile(path, saver.toString());
    }

    public static String listToStringFormat(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        return String.join("-", list) + "-";
    }

    private void responseSender(SocketChannel channel, int ssss, String mess) throws IOException {
        messageRespon.setLength(0);
        messageRespon.append(ssss);
        messageRespon.append("\n");
        if (mess != null) {
            messageRespon.append(mess);
            messageRespon.append("\n");
        }
        ByteBuffer byteBuffer = charset.encode(CharBuffer.wrap(messageRespon));
        channel.write(byteBuffer);
    }
    public static void main(String[] args) {
        new Server("localhost", 4001);
    }
}
