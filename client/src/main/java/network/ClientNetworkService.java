package network;

import commands.Command;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientNetworkService {
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;

    public ClientNetworkService(String host, int port) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.serverAddress = new InetSocketAddress(host, port);
        System.out.println("Подключение к серверу " + host + ":" + port);
    }

    public String sendCommand(Command command) throws IOException, ClassNotFoundException, InterruptedException {
        byte[] data = serialize(command);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, serverAddress);

        ByteBuffer responseBuffer = ByteBuffer.allocate(65535);

        for (int i = 0; i < 3; i++) {
            responseBuffer.clear();

            try {
                InetSocketAddress sender = (InetSocketAddress) channel.receive(responseBuffer);

                if (sender != null) {
                    responseBuffer.flip();
                    byte[] respData = new byte[responseBuffer.remaining()];
                    responseBuffer.get(respData);
                    return (String) deserialize(respData);
                }
            } catch (IOException e) {
                System.err.println("Ошибка сети при ожидании ответа: " + e.getMessage());
            }
            Thread.sleep(1000);
        }
        throw new IOException("Сервер не отвечает после 3 попыток.");
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

    public void close() throws IOException {
        channel.close();
    }
}